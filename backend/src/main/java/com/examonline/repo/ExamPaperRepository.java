package com.examonline.repo;

import com.examonline.domain.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {
    List<ExamPaper> findByPublishedTrueOrderByStartTimeDesc();
    List<ExamPaper> findByPublishedTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalDateTime start, LocalDateTime end);
}

