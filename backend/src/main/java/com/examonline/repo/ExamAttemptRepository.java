package com.examonline.repo;

import com.examonline.domain.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByStudentUsernameOrderByCreatedAtDesc(String studentUsername);
    Optional<ExamAttempt> findByPaperIdAndStudentUsername(Long paperId, String studentUsername);
}

