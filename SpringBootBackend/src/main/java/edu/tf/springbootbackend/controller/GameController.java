package edu.tf.springbootbackend.controller;

import edu.tf.springbootbackend.dto.*;
import edu.tf.springbootbackend.service.GameSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameSessionService gameSessionService;

    public GameController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<GameStartResponse> startGame(@RequestBody GameStartRequest request) {
        return ResponseEntity.ok(gameSessionService.startGame(request.getPlayerId()));
    }

    @PostMapping("/score")
    public ResponseEntity<GameScoreResponse> updateScore(@RequestBody GameScoreRequest request) {
        return ResponseEntity.ok(gameSessionService.updateScore(request.getSessionId(), request.getScoreDelta()));
    }

    @PostMapping("/end")
    public ResponseEntity<GameEndResponse> endGame(@RequestBody GameEndRequest request) {
        return ResponseEntity.ok(gameSessionService.endGame(request.getSessionId()));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveGame>> getActiveGames() {
        return ResponseEntity.ok(gameSessionService.getActiveGames());
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(@RequestBody GameEndRequest request) {
        gameSessionService.heartbeat(request.getSessionId());
        return ResponseEntity.ok().build();
    }
}
