package edu.tf.springbootbackend.dto;

public class GameEndRequest {
    private String sessionId;

    public GameEndRequest() {}

    public GameEndRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
