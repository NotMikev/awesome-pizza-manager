package com.awesome.pizza.order.manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesome.pizza.order.manager.entity.ApiAuditLog;

@Repository
public interface ApiAuditLogRepository extends JpaRepository<ApiAuditLog, Long> {

    Optional<ApiAuditLog> findByCorrelationId(String correlationId);
}
