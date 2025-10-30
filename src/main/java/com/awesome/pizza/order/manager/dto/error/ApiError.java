package com.awesome.pizza.order.manager.dto.error;

import lombok.Data;

@Data
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;
    private long timestamp;
    private String correlationId;

    public ApiError(int status, String error, String message, String path, long timestamp, String correlationId) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.correlationId = correlationId;
    }

}
