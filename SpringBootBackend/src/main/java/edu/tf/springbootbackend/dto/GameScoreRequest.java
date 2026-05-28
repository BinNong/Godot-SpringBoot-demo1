package edu.tf.springbootbackend.dto;

public class GameScoreRequest {
    private String sessionId;
    private Integer scoreDelta;

    public GameScoreRequest() {}

    public GameScoreRequest(String sessionId, Integer scoreDelta) {
        this.sessionId = sessionId;
        this.scoreDelta = scoreDelta;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Integer getScoreDelta() { return scoreDelta; }
    public void setScoreDelta(Integer scoreDelta) { this.scoreDelta = scoreDelta; }
}
