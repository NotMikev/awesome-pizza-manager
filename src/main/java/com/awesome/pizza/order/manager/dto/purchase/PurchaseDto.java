package com.awesome.pizza.order.manager.dto.purchase;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Represents a pizza order")
@Data
public class PurchaseDto {

    @Schema(description = "Unique code", example = "uuid-1234-abcd")
    private String code;

    @NotBlank(message = "Pizza type is required")
    @Size(min = 3, max = 50, message = "Pizza type must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s-]+$", message = "Pizza type can only contain letters, numbers, spaces and hyphens")
    @Schema(description = "Pizza type", example = "Margherita")
    private String pizza;

    @Schema(description = "Order status", example = "NEW")
    private String status;

    @Schema(description = "Order creation time", example = "2025-10-28T22:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Order last update time", example = "2025-10-28T22:00:00")
    private LocalDateTime updatedAt;

}
