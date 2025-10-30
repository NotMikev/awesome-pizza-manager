package com.awesome.pizza.order.manager.exception;

public class AuditPersistenceException extends RuntimeException {

    public AuditPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
