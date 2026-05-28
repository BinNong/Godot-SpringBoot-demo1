package edu.tf.springbootbackend.dto;

public class GameEndResponse {
    private String sessionId;
    private Integer finalScore;
    private Boolean isNewBest;
    private Integer allTimeBest;

    public GameEndResponse() {}

    public GameEndResponse(String sessionId, Integer finalScore, Boolean isNewBest, Integer allTimeBest) {
        this.sessionId = sessionId;
        this.finalScore = finalScore;
        this.isNewBest = isNewBest;
        this.allTimeBest = allTimeBest;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Integer getFinalScore() { return finalScore; }
    public void setFinalScore(Integer finalScore) { this.finalScore = finalScore; }
    public Boolean getIsNewBest() { return isNewBest; }
    public void setIsNewBest(Boolean isNewBest) { this.isNewBest = isNewBest; }
    public Integer getAllTimeBest() { return allTimeBest; }
    public void setAllTimeBest(Integer allTimeBest) { this.allTimeBest = allTimeBest; }
}
