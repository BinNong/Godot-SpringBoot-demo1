package edu.tf.springbootbackend.dto;

public class GameStartResponse {
    private String sessionId;
    private String username;
    private Long startTime;

    public GameStartResponse() {}

    public GameStartResponse(String sessionId, String username, Long startTime) {
        this.sessionId = sessionId;
        this.username = username;
        this.startTime = startTime;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
}
