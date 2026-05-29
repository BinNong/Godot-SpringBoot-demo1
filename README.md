# 像素跑酷 — Godot + Spring Boot 实时游戏

这是一个全栈游戏项目，包含：

- **Godot 4.6** 2D 平台游戏（前端）
- **Spring Boot 3.5** REST API + WebSocket 后端
- **Vue 3** 前端仪表盘
- **Redis** 会话存储 + 排行榜
- **PostgreSQL** 用户数据持久化

## 项目结构

```
Godot-SpringBoot-demo1/
├── First Game/                  # Godot 4.6 项目
│   ├── scripts/
│   │   ├── game_manager.gd      # 游戏会话管理 + API 通信
│   │   ├── player.gd           # 玩家控制（跳跃/移动）
│   │   ├── coin.gd             # 金币收集逻辑
│   │   ├── slime.gd            # 敌人逻辑
│   │   └── killzone.gd         # 死亡区域，触发结算
│   ├── scenes/
│   │   └── game.tscn           # 主游戏场景
│   └── player_config.cfg        # 玩家认证配置
├── SpringBootBackend/           # Spring Boot 后端
│   └── src/main/java/edu/tf/springbootbackend/
│       ├── controller/
│       │   ├── AuthController.java    # 注册/登录
│       │   ├── GameController.java    # 游戏会话 API
│       │   └── ScoreController.java   # 排行榜 API
│       ├── service/
│       │   ├── GameSessionService.java   # 会话管理 + 清理
│       │   ├── LeaderboardService.java   # 排行榜逻辑
│       │   └── AuthService.java
│       ├── dto/                  # 请求/响应对象
│       ├── entity/               # JPA 实体
│       ├── repository/           # 数据库访问
│       └── config/
│           ├── SecurityConfig.java   # JWT 认证
│           ├── WebSocketConfig.java   # WebSocket/STOMP
│           ├── CorsConfig.java
│           └── RedisConfig.java
└── frontend/                    # Vue 3 前端
    └── src/
        ├── views/
        │   ├── LoginView.vue         # 登录页
        │   ├── HomeView.vue          # 首页
        │   ├── LeaderboardView.vue  # 排行榜
        │   ├── ActiveGamesView.vue   # 实时玩家
        │   └── GodotConfigView.vue   # Godot 配置页
        ├── stores/
        │   └── leaderboard.ts       # Pinia store
        └── services/
            ├── auth.ts
            └── websocket.ts
```

## 架构概览

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   Godot Game    │────────▶│  Spring Boot   │────────▶│   PostgreSQL    │
│   (HTTP/WebSocket)         │   REST + WS     │         │   (用户/分数)    │
└─────────────────┘         └────────┬────────┘         └─────────────────┘
                                     │
                              ┌──────▼──────┐
                              │    Redis    │
                              │ (会话/排行)  │
                              └─────────────┘
                                     │
                              ┌──────▼──────┐
                              │  Vue 3 SPA  │
                              │  仪表盘     │
                              └─────────────┘
```

## 技术栈

| 组件 | 技术 |
|------|------|
| 游戏引擎 | Godot 4.6 (GDScript) |
| 后端框架 | Spring Boot 3.5.14 (Java 17) |
| 前端框架 | Vue 3 + TypeScript + Vite |
| 数据库 | PostgreSQL |
| 缓存/会话 | Redis |
| 实时通信 | WebSocket (STOMP) |
| 认证 | JWT (jjwt 0.12.6) |
| 构建工具 | Maven (后端) / npm (前端) |

## API 端点

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 注册用户 |
| POST | `/api/auth/login` | 登录，返回 JWT |

### 游戏会话

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/game/start` | 开始新游戏会话 |
| POST | `/api/game/score` | 提交得分（+1金币） |
| POST | `/api/game/end` | 结束当前会话 |
| POST | `/api/game/heartbeat` | 心跳保活（客户端每15秒） |
| GET | `/api/game/active` | 获取所有活跃游戏 |

### 排行榜

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/leaderboard` | 获取前10名 |
| GET | `/api/leaderboard/{playerId}` | 获取指定玩家排名 |

### WebSocket 主题

| 主题 | 触发条件 |
|------|---------|
| `/topic/leaderboard` | 玩家打破历史最高分 |
| `/topic/active-games` | 任何游戏会话变化（开始/得分/结束） |

## Redis 数据结构

| Key | 类型 | 用途 |
|-----|------|------|
| `game:session:{sessionId}` | Hash | 活跃游戏数据：playerId, username, currentScore, startTime, lastHeartbeat, status |
| `game:active` | Set | 所有活跃 sessionId |
| `leaderboard:scores` | ZSET | 玩家历史最高分（只在打破记录时更新） |

## 快速启动

### 1. 启动基础设施

确保以下服务已运行：

- **PostgreSQL** — 数据库服务器，详见 `application.properties.example`
- **Redis** — 缓存/会话服务器，详见 `application.properties.example`

或修改 `SpringBootBackend/src/main/resources/application.properties` 中的连接配置。

### 2. 启动后端

```bash
cd SpringBootBackend
./mvnw spring-boot:run
```

后端运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`。

### 4. 运行 Godot 游戏

1. 用 Godot Engine 4.6 打开 `First Game/` 目录
2. 按 `F5` 运行游戏
3. 收集金币，观察分数提交到后端
4. 死亡后游戏自动重置，分数不会累加到新一局

## 前端使用流程

1. **注册/登录** — 访问 `http://localhost:5173`
2. **配置 Godot** — 进入「游戏配置」页面，下载生成的 `player_config.cfg`，放入 `First Game/` 目录
3. **开始游戏** — Godot 加载时自动开始会话并提交分数
4. **查看排行榜** — 排行榜页面显示历史最高分
5. **查看活跃玩家** — 实时查看当前所有在线玩家和分数

## 游戏会话机制

### 正常流程

1. Godot 加载 `game_manager.gd` → 自动调用 `/api/game/start`
2. 玩家收集金币 → 每次 +1 分，调用 `/api/game/score`
3. 玩家死亡 → `killzone.gd` 调用 `/api/game/end`，场景重载
4. 新会话开始，分数从 0 计算

### 异常断开处理

Godot 每 15 秒发送一次心跳到 `/api/game/heartbeat`。后端每 10 秒检查所有活跃会话，超过 30 秒无心跳的会话被视为异常断开，后端自动清理该会话并结算分数。

### 分数逻辑

- 每局游戏独立计分，死亡后重新开始是新的一局
- 排行榜只记录**历史最高分**，只在打破记录时更新
- 排行榜更新通过 WebSocket 广播，前端实时刷新

## 核心文件说明

### Godot 端

| 文件 | 职责 |
|------|------|
| `game_manager.gd` | 管理游戏会话、分数提交、心跳、API 通信 |
| `player.gd` | 玩家物理移动和跳跃 |
| `coin.gd` | 金币检测，触发 `game_manager.add_point()` |
| `killzone.gd` | 死亡区域，延迟 0.5 秒重载场景确保 `end_game` 请求发出 |

### 后端

| 文件 | 职责 |
|------|------|
| `GameSessionService.java` | 游戏会话 CRUD、心跳追踪、异常清理 |
| `LeaderboardService.java` | 排行榜更新、广播 |
| `GameController.java` | 游戏 API 端点 |
| `SecurityConfig.java` | JWT 过滤器、CORS、路由权限 |

### 前端

| 文件 | 职责 |
|------|------|
| `ActiveGamesView.vue` | 实时活跃玩家列表（WebSocket 驱动） |
| `LeaderboardView.vue` | 排行榜展示 |
| `websocket.ts` | WebSocket 连接管理（leaderboard + active-games） |

## 注意事项

- Godot 的 `HTTPRequest` 只支持串行请求，每次新请求前需调用 `cancel_request()`
- 场景重载必须延迟（0.5s timer）确保 `end_game` HTTP 请求在重载前发出
- JWT 认证通过 `Authorization: Bearer <token>` Header 传递
- `player_config.cfg` 包含 `player_id` 和 `auth_token`，是连接游戏和后端的凭证
