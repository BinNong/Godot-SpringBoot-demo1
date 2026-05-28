package edu.tf.springbootbackend.dto;

public class ScoreUpdateMessage {
    private Long playerId;
    private String username;
    private Integer totalScore;
    private Integer rank;

    public ScoreUpdateMessage() {}

    public ScoreUpdateMessage(Long playerId, String username, Integer totalScore, Integer rank) {
        this.playerId = playerId;
        this.username = username;
        this.totalScore = totalScore;
        this.rank = rank;
    }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
}
