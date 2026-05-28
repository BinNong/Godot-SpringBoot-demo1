package edu.tf.springbootbackend.dto;

public class ActiveGame {
    private String sessionId;
    private Long playerId;
    private String username;
    private Integer currentScore;
    private Long startTime;
    private Integer durationSeconds;

    public ActiveGame() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getCurrentScore() { return currentScore; }
    public void setCurrentScore(Integer currentScore) { this.currentScore = currentScore; }
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
}
