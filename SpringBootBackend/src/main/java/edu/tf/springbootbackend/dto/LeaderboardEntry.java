package edu.tf.springbootbackend.dto;

public class LeaderboardEntry {
    private Integer rank;
    private Long playerId;
    private String username;
    private Integer totalScore;

    public LeaderboardEntry() {}

    public LeaderboardEntry(Integer rank, Long playerId, String username, Integer totalScore) {
        this.rank = rank;
        this.playerId = playerId;
        this.username = username;
        this.totalScore = totalScore;
    }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
}
