# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a monorepo containing:
- **First Game** — A Godot 4.6 2D platformer (GDScript)
- **SpringBootBackend** — A Spring Boot REST API backend (Java 17, Maven)

## First Game (Godot)

**Run the game:** Open the project in Godot Engine 4.6 or later, or press F5 in the editor.
- Main scene: `scenes/game.tscn`
- Autoload: `scenes/music.tscn` (background music)

**Key scripts:**
- `scripts/player.gd` — CharacterBody2D with jump/run physics
- `scripts/game_manager.gd` — Game state + score submission to backend
- `scripts/slime.gd`, `scripts/coin.gd`, `scripts/killzone.gd` — Game entities

**Player config:** `player_config.cfg` — contains `player_id` and `auth_token` for score submission

**Controls:** Arrow keys or WASD to move, Spacebar to jump.

**Input bindings** are defined in `project.godot` under `[input]`: `move_left`, `move_right`, `jump`.

## SpringBootBackend

**Build and run:**
```bash
cd SpringBootBackend
./mvnw spring-boot:run
```

**Run tests:**
手动在idea中运行

**Package:**
手动在idea中使用maven打包

The application uses Spring Boot 3.5.14 with Java 17. The main class is `edu.tf.springbootbackend.SpringBootBackendApplication`.

## Architecture

- Godot game starts session on load, submits scores via POST to `/api/game/score` with sessionId
- On death, killzone delays scene reload (0.5s) after calling `/api/game/end` to ensure HTTP request is sent
- Spring Boot stores per-session data in Redis Hash (`game:session:{sessionId}`) using StringRedisTemplate and active set (`game:active`)
- Leaderboard (`leaderboard:scores` ZSET) only updates when player beats their all-time best
- Vue dashboard at `/leaderboard` shows all-time best scores, `/active-games` shows current players
- WebSocket topics: `/topic/leaderboard` (record-breaking updates), `/topic/active-games` (active sessions)
- User authentication: register/login via `/api/auth/register` and `/api/auth/login`
- Database: PostgreSQL at `nongbin.site:5432/first_game` (user: fbdtp)
- Redis: `nongbin.site:6380` (user: dog)

## Game Session Heartbeat

- Godot sends heartbeat every 15 seconds via `POST /api/game/heartbeat` to keep session alive
- Backend cleans up sessions with no heartbeat for 30 seconds (runs every 10 seconds)
- This ensures active game data is cleaned up even when Godot engine stops unexpectedly

## Godot GameManager Notes

- HTTPRequest only handles one request at a time — use `cancel_request()` before new requests
- Scene reload must be deferred (via ReloadTimer ~0.5s) after `end_game()` to ensure HTTP request is sent
- GameManager path from killzone: `get_node("/root/Game/GameManager")` (absolute path, not scene unique)
- Heartbeat timer is created in `_setup_heartbeat_timer()` and cleaned up on `NOTIFICATION_EXIT_TREE`
