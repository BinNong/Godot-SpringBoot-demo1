package edu.tf.springbootbackend.dto;

public class GameScoreResponse {
    private String sessionId;
    private Integer currentScore;

    public GameScoreResponse() {}

    public GameScoreResponse(String sessionId, Integer currentScore) {
        this.sessionId = sessionId;
        this.currentScore = currentScore;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Integer getCurrentScore() { return currentScore; }
    public void setCurrentScore(Integer currentScore) { this.currentScore = currentScore; }
}
