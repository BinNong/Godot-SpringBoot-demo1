package edu.tf.springbootbackend.service;

import edu.tf.springbootbackend.dto.ActiveGame;
import edu.tf.springbootbackend.dto.GameEndResponse;
import edu.tf.springbootbackend.dto.GameScoreResponse;
import edu.tf.springbootbackend.dto.GameStartResponse;
import edu.tf.springbootbackend.entity.User;
import edu.tf.springbootbackend.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;

@Service
public class GameSessionService {

    private static final String SESSION_KEY_PREFIX = "game:session:";
    private static final String ACTIVE_SET_KEY = "game:active";
    private static final long HEARTBEAT_TIMEOUT_SECONDS = 30;

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameSessionService(StringRedisTemplate redisTemplate,
                             UserRepository userRepository,
                             LeaderboardService leaderboardService,
                             SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.leaderboardService = leaderboardService;
        this.messagingTemplate = messagingTemplate;
    }

    public GameStartResponse startGame(Long playerId) {
        String sessionId = UUID.randomUUID().toString();
        User user = userRepository.findById(playerId).orElse(null);
        String username = user != null ? user.getUsername() : "Unknown";
        long startTime = System.currentTimeMillis();

        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, "playerId", playerId.toString());
        redisTemplate.opsForHash().put(sessionKey, "username", username);
        redisTemplate.opsForHash().put(sessionKey, "currentScore", "0");
        redisTemplate.opsForHash().put(sessionKey, "startTime", String.valueOf(startTime));
        redisTemplate.opsForHash().put(sessionKey, "status", "active");
        redisTemplate.opsForHash().put(sessionKey, "lastHeartbeat", String.valueOf(startTime));
        redisTemplate.opsForSet().add(ACTIVE_SET_KEY, sessionId);

        broadcastActiveGames();

        return new GameStartResponse(sessionId, username, startTime);
    }

    public GameScoreResponse updateScore(String sessionId, Integer scoreDelta) {
        if (sessionId == null || sessionId.isBlank()) {
            System.out.println("updateScore called with empty sessionId");
            return new GameScoreResponse(sessionId, 0);
        }

        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(sessionKey);

        if (hash == null || hash.isEmpty()) {
            System.out.println("Session not found in Redis: " + sessionId);
            return new GameScoreResponse(sessionId, 0);
        }

        // 如果 currentScore 不存在，先初始化
        if (!hash.containsKey("currentScore")) {
            redisTemplate.opsForHash().put(sessionKey, "currentScore", "0");
            hash.put("currentScore", "0");
        }

        Long newScore = redisTemplate.opsForHash().increment(sessionKey, "currentScore", scoreDelta);
        redisTemplate.opsForHash().put(sessionKey, "lastHeartbeat", String.valueOf(System.currentTimeMillis()));
        broadcastActiveGames();
        return new GameScoreResponse(sessionId, newScore != null ? newScore.intValue() : 0);
    }

    public void heartbeat(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(sessionKey);
        if (hash != null && !hash.isEmpty()) {
            redisTemplate.opsForHash().put(sessionKey, "lastHeartbeat", String.valueOf(System.currentTimeMillis()));
        }
    }

    public GameEndResponse endGame(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(sessionKey);

        if (hash == null || hash.isEmpty()) {
            return new GameEndResponse(sessionId, 0, false, 0);
        }

        Integer finalScore = Integer.parseInt((String) hash.get("currentScore"));
        Long playerId = Long.parseLong((String) hash.get("playerId"));

        redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, sessionId);
        redisTemplate.delete(sessionKey);

        boolean isNewBest = leaderboardService.updateBestScore(playerId, finalScore);
        Integer allTimeBest = leaderboardService.getPlayerRank(playerId).getTotalScore();

        if (isNewBest) {
            leaderboardService.broadcastLeaderboard();
        }
        broadcastActiveGames();

        return new GameEndResponse(sessionId, finalScore, isNewBest, allTimeBest);
    }

    public List<ActiveGame> getActiveGames() {
        Set<String> sessionIds = redisTemplate.opsForSet().members(ACTIVE_SET_KEY);
        List<ActiveGame> games = new ArrayList<>();
        long now = System.currentTimeMillis();

        if (sessionIds == null) return games;

        for (String sessionId : sessionIds) {
            Map<Object, Object> hash = redisTemplate.opsForHash().entries(SESSION_KEY_PREFIX + sessionId);
            if (hash != null && !hash.isEmpty()) {
                ActiveGame game = new ActiveGame();
                game.setSessionId(sessionId);
                game.setPlayerId(Long.parseLong(String.valueOf(hash.get("playerId"))));
                game.setUsername(String.valueOf(hash.get("username")));
                game.setCurrentScore(Integer.parseInt(String.valueOf(hash.get("currentScore"))));
                game.setStartTime(Long.parseLong(String.valueOf(hash.get("startTime"))));
                game.setDurationSeconds((int) ((now - game.getStartTime()) / 1000));
                games.add(game);
            } else {
                redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, sessionId);
            }
        }
        return games;
    }

    private void broadcastActiveGames() {
        messagingTemplate.convertAndSend("/topic/active-games", getActiveGames());
    }

    @Scheduled(fixedRate = 10000)
    public void cleanupStaleSessions() {
        Set<String> sessionIds = redisTemplate.opsForSet().members(ACTIVE_SET_KEY);
        if (sessionIds == null || sessionIds.isEmpty()) return;

        long now = System.currentTimeMillis();
        long timeoutMs = HEARTBEAT_TIMEOUT_SECONDS * 1000;

        for (String sessionId : sessionIds) {
            String sessionKey = SESSION_KEY_PREFIX + sessionId;
            Map<Object, Object> hash = redisTemplate.opsForHash().entries(sessionKey);
            if (hash == null || hash.isEmpty()) {
                redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, sessionId);
                continue;
            }

            String lastHeartbeatStr = (String) hash.get("lastHeartbeat");
            if (lastHeartbeatStr == null) {
                lastHeartbeatStr = (String) hash.get("startTime");
            }

            if (lastHeartbeatStr != null) {
                long lastHeartbeat = Long.parseLong(lastHeartbeatStr);
                if (now - lastHeartbeat > timeoutMs) {
                    System.out.println("Cleaning up stale session: " + sessionId + " (no heartbeat for " + (now - lastHeartbeat) / 1000 + "s)");
                    Integer finalScore = Integer.parseInt((String) hash.get("currentScore"));
                    Long playerId = Long.parseLong((String) hash.get("playerId"));
                    redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, sessionId);
                    redisTemplate.delete(sessionKey);
                    leaderboardService.updateBestScore(playerId, finalScore);
                    leaderboardService.broadcastLeaderboard();
                }
            }
        }
    }
}
