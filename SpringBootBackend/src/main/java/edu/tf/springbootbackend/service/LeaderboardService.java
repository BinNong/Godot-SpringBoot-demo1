package edu.tf.springbootbackend.service;

import edu.tf.springbootbackend.dto.LeaderboardEntry;
import edu.tf.springbootbackend.dto.ScoreUpdateMessage;
import edu.tf.springbootbackend.entity.User;
import edu.tf.springbootbackend.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final String LEADERBOARD_KEY = "leaderboard:scores";
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LeaderboardService(RedisTemplate<String, Object> redisTemplate,
                              UserRepository userRepository,
                              SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void addScore(Long playerId, Integer scoreDelta) {
        redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, playerId.toString(), scoreDelta);
        broadcastLeaderboard();
    }

    public boolean updateBestScore(Long playerId, Integer sessionScore) {
        Double currentBest = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, playerId.toString());
        if (currentBest == null || sessionScore > currentBest) {
            redisTemplate.opsForZSet().add(LEADERBOARD_KEY, playerId.toString(), sessionScore);
            return true;
        }
        return false;
    }

    public List<LeaderboardEntry> getTopN(int n) {
        Set<ZSetOperations.TypedTuple<Object>> tuples =
            redisTemplate.opsForZSet().reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);
        List<LeaderboardEntry> entries = new ArrayList<>();
        if (tuples == null) return entries;
        int rank = 1;
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
            Long playerId = Long.parseLong(tuple.getValue().toString());
            User user = userRepository.findById(playerId).orElse(null);
            String username = user != null ? user.getUsername() : "Unknown";
            entries.add(new LeaderboardEntry(rank++, playerId, username, tuple.getScore().intValue()));
        }
        return entries;
    }

    public LeaderboardEntry getPlayerRank(Long playerId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, playerId.toString());
        Double score = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, playerId.toString());
        if (rank == null) return null;
        User user = userRepository.findById(playerId).orElse(null);
        String username = user != null ? user.getUsername() : "Unknown";
        return new LeaderboardEntry(rank.intValue() + 1, playerId, username, score != null ? score.intValue() : 0);
    }

    public void broadcastLeaderboard() {
        List<LeaderboardEntry> top = getTopN(10);
        ScoreUpdateMessage msg = new ScoreUpdateMessage();
        msg.setPlayerId(null);
        msg.setUsername(null);
        msg.setTotalScore(null);
        msg.setRank(null);
        messagingTemplate.convertAndSend("/topic/leaderboard", top);
    }
}
