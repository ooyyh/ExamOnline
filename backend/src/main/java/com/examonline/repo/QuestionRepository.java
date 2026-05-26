package com.examonline.repo;

import com.examonline.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectOrderByIdAsc(String subject);
    List<Question> findByTypeAndSubjectAndDifficulty(String type, String subject, String difficulty);
    List<Question> findBySubjectAndDifficulty(String subject, String difficulty);
}

