package com.examonline.repo;

import com.examonline.domain.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {
    List<AttemptAnswer> findByAttemptIdOrderByQuestionIdAsc(Long attemptId);
    Optional<AttemptAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
    List<AttemptAnswer> findByAttemptId(Long attemptId);
}

