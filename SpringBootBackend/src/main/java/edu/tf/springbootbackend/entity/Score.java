package edu.tf.springbootbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long oderId;

    private String sessionId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Score() {}

    public Score(Long oderId, String sessionId, Integer score) {
        this.oderId = oderId;
        this.sessionId = sessionId;
        this.score = score;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOderId() { return oderId; }
    public void setOderId(Long oderId) { this.oderId = oderId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
