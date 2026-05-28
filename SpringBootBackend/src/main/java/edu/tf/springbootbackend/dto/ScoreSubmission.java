package edu.tf.springbootbackend.dto;

public class ScoreSubmission {
    private Long playerId;
    private Integer scoreDelta;
    private String sessionId;

    public ScoreSubmission() {}

    public ScoreSubmission(Long playerId, Integer scoreDelta, String sessionId) {
        this.playerId = playerId;
        this.scoreDelta = scoreDelta;
        this.sessionId = sessionId;
    }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public Integer getScoreDelta() { return scoreDelta; }
    public void setScoreDelta(Integer scoreDelta) { this.scoreDelta = scoreDelta; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
