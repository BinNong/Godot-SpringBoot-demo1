package edu.tf.springbootbackend.repository;

import edu.tf.springbootbackend.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByOderIdOrderByTimestampDesc(Long oderId);
}
