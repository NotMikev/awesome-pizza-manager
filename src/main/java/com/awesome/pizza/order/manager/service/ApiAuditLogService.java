package com.awesome.pizza.order.manager.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.awesome.pizza.order.manager.entity.ApiAuditLog;
import com.awesome.pizza.order.manager.repository.ApiAuditLogRepository;

@Service
public class ApiAuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditLogService.class);

    private final ApiAuditLogRepository repository;

    public ApiAuditLogService(ApiAuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(ApiAuditLog auditLog) {
        try {
            repository.save(auditLog);
            logger.debug("Saved audit log for correlationId={}", auditLog.getCorrelationId());
        } catch (Exception e) {
            logger.error("Error saving audit log for correlationId={}", auditLog == null ? "<null>" : auditLog.getCorrelationId(), e);
        }
    }

    public Optional<ApiAuditLog> findByCorrelationId(String correlationId) {
        return repository.findByCorrelationId(correlationId);
    }

}
