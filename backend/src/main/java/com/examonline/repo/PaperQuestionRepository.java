package com.examonline.repo;

import com.examonline.domain.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperQuestionRepository extends JpaRepository<PaperQuestion, Long> {
    List<PaperQuestion> findByPaperIdOrderByOrderNoAsc(Long paperId);
    void deleteByPaperId(Long paperId);
}

