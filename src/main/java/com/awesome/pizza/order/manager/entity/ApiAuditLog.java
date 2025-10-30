package com.awesome.pizza.order.manager.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "API_AUDIT_LOG")
@Data
@NoArgsConstructor
public class ApiAuditLog {

    public ApiAuditLog(String correlationId, LocalDateTime timestamp, String method, String path,
            String requestBody, int responseStatus, String responseBody, String exceptionDetail) {
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.method = method;
        this.path = path;
        this.requestBody = requestBody;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.exceptionDetail = exceptionDetail;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", nullable = false, length = 36)
    private String correlationId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_status", nullable = false)
    private int responseStatus;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "exception_detail", columnDefinition = "TEXT")
    private String exceptionDetail;

}
