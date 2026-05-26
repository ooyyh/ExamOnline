package com.examonline.service;

import com.examonline.domain.OperationLog;
import com.examonline.repo.OperationLogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    private final OperationLogRepository repository;

    public LogService(OperationLogRepository repository) {
        this.repository = repository;
    }

    public void record(String actorUsername, String action, String targetType, String targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setActorUsername(actorUsername);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        repository.save(log);
    }
}

