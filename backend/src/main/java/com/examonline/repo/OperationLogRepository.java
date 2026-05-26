package com.examonline.repo;

import com.examonline.domain.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findTop100ByOrderByCreatedAtDesc();
}

