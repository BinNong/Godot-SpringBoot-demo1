package edu.tf.springbootbackend.controller;

import edu.tf.springbootbackend.dto.LeaderboardEntry;
import edu.tf.springbootbackend.dto.ScoreSubmission;
import edu.tf.springbootbackend.service.LeaderboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScoreController {

    private final LeaderboardService leaderboardService;

    public ScoreController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/scores")
    public ResponseEntity<Void> submitScore(@RequestBody ScoreSubmission submission, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).build();
        }
        leaderboardService.addScore(submission.getPlayerId(), submission.getScoreDelta());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getTopN(10));
    }

    @GetMapping("/leaderboard/{playerId}")
    public ResponseEntity<LeaderboardEntry> getPlayerRank(@PathVariable Long playerId) {
        LeaderboardEntry entry = leaderboardService.getPlayerRank(playerId);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(entry);
    }
}
