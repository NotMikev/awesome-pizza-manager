package com.awesome.pizza.order.manager.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.awesome.pizza.order.manager.dto.error.ApiError;
import com.awesome.pizza.order.manager.exception.AuditPersistenceException;
import com.awesome.pizza.order.manager.exception.PurchaseNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.awesome.pizza.order.manager")
public class GlobalExceptionHandler {

    @ExceptionHandler(PurchaseNotFoundException.class)
    public ResponseEntity<ApiError> handlePurchaseNotFound(PurchaseNotFoundException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeExceptions(RuntimeException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiError> handleJacksonJsonProcessing(JsonProcessingException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuditPersistenceException.class)
    public ResponseEntity<ApiError> handleAuditPersistence(AuditPersistenceException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> buildErrorResponse(Exception ex, HttpServletRequest req, HttpStatus status) {

        String correlationId = (String) req.getAttribute("CORRELATION_ID");

        if (correlationId == null) {
            correlationId = "N/A";
        }

        ApiError apiError = new ApiError(
                status.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                req.getRequestURI(),
                System.currentTimeMillis(),
                correlationId
        );

        return ResponseEntity
                .status(status)
                .body(apiError);
    }
}
