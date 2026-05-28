package edu.tf.springbootbackend.dto;

public class GameStartRequest {
    private Long playerId;

    public GameStartRequest() {}

    public GameStartRequest(Long playerId) {
        this.playerId = playerId;
    }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
}
