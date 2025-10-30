package com.awesome.pizza.order.manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.awesome.pizza.order.manager.dto.purchase.PurchaseDto;
import com.awesome.pizza.order.manager.dto.error.ApiError;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.awesome.pizza.order.manager.service.PurchaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/purchase")
@Tag(name = "Purchase", description = "APIs for managing the pizza order lifecycle - from creation to delivery")
@Validated
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new pizza order",
            description = "Creates a new pizza order with the specified pizza type. The order will be assigned a unique code and start with NEW status.",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Order successfully created",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PurchaseDto.class),
                                examples = @ExampleObject(value = """
                        {
                            "code": "uuid-1234-abcd",
                            "pizza": "Margherita",
                            "status": "NEW",
                            "createdAt": "2025-10-28T22:00:00",
                            "updatedAt": "2025-10-28T22:00:00"
                        }
                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input - pizza type is mandatory and must be between 3 and 50 characters, containing only letters, numbers, spaces and hyphens",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ApiError.class),
                                examples = @ExampleObject(value = """
                                {
                                    "status": 400,
                                    "error": "ValidationException",
                                    "message": "Pizza type is required",
                                    "path": "/api/purchase",
                                    "timestamp": 1698619200000,
                                    "correlationId": "abc-123-xyz"
                                }
                                """
                                )
                        )
                )
            }
    )
    public ResponseEntity<PurchaseDto> createPurchase(
            @Parameter(description = "Type of pizza to order", required = true, example = "Margherita")
            @NotBlank(message = "Pizza type is required")
            @Size(min = 3, max = 50, message = "Pizza type must be between 3 and 50 characters")
            @Pattern(regexp = "^[a-zA-Z0-9\\s-]+$", message = "Pizza type can only contain letters, numbers, spaces and hyphens")
            @RequestParam String pizza) {

        logger.debug("createPurchase controller received pizza={}", pizza);
        PurchaseDto purchase = purchaseService.createPurchase(pizza);
        logger.debug("createPurchase controller returning={}", purchase);
        return new ResponseEntity<>(purchase, HttpStatus.CREATED);
    }

    @PostMapping("/next")
    @Operation(
            summary = "Get the next pizza order in queue",
            description = "Returns the next pizza order to be prepared and updates its status to IN_PROGRESS.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Next order retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PurchaseDto.class),
                                examples = @ExampleObject(value = """
                        {
                            "code": "uuid-1234-abcd",
                            "pizza": "Margherita",
                            "status": "IN_PROGRESS",
                            "createdAt": "2025-10-28T22:00:00",
                            "updatedAt": "2025-10-28T22:05:00"
                        }
                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No pending orders found"
                )
            }
    )
    public ResponseEntity<PurchaseDto> takeNextPurchase() {
        logger.debug("takeNextPurchase controller called");
        PurchaseDto dto = purchaseService.takeNextPurchase();
        logger.debug("takeNextPurchase controller returning={}", dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/next/{code}")
    @Operation(
            summary = "Get a specific pizza order from the queue",
            description = "Returns the specified order if it's in NEW status and updates its status to IN_PROGRESS.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Order retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PurchaseDto.class),
                                examples = @ExampleObject(value = """
                        {
                            "code": "uuid-1234-abcd",
                            "pizza": "Margherita",
                            "status": "IN_PROGRESS",
                            "createdAt": "2025-10-28T22:00:00",
                            "updatedAt": "2025-10-28T22:05:00"
                        }
                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Order not found or not in NEW status"
                )
            }
    )
    public ResponseEntity<PurchaseDto> takeNextPurchaseByCode(
            @Parameter(description = "Unique order code", required = true, example = "uuid-1234-abcd")
            @NotBlank(message = "Order code is required")
            @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Order code must contain only letters, numbers and hyphens")
            @Size(min = 8, max = 50, message = "Order code must be between 8 and 50 characters")
            @PathVariable String code) {
        logger.debug("takeNextPurchaseByCode controller called with code={}", code);
        PurchaseDto dto = purchaseService.takeNextPurchaseByCode(code);
        logger.debug("takeNextPurchaseByCode controller returning={}", dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{code}/ready")
    @Operation(
            summary = "Mark a pizza order as ready",
            description = "Updates the status of the order identified by the provided code to READY.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Order marked as ready",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PurchaseDto.class),
                                examples = @ExampleObject(value = """
                        {
                            "code": "uuid-1234-abcd",
                            "pizza": "Margherita",
                            "status": "READY",
                            "createdAt": "2025-10-28T22:00:00",
                            "updatedAt": "2025-10-28T22:10:00"
                        }
                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Order with specified code not found"
                )
            }
    )
    public ResponseEntity<PurchaseDto> markReady(
            @Parameter(description = "Unique order code", required = true, example = "uuid-1234-abcd")
            @NotBlank(message = "Order code is required")
            @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Order code must contain only letters, numbers and hyphens")
            @Size(min = 8, max = 50, message = "Order code must be between 8 and 50 characters")
            @PathVariable String code) {
        logger.debug("markReady controller called with code={}", code);
        PurchaseDto purchase = purchaseService.markPurchaseReady(code);
        logger.debug("markReady controller returning={}", purchase);
        return ResponseEntity.ok(purchase);
    }

    @GetMapping("/{code}")
    @Operation(
            summary = "Check the status of a pizza order",
            description = "Returns the current status of the order with the given order code.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Order status retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PurchaseDto.class),
                                examples = @ExampleObject(value = """
                        {
                            "code": "uuid-1234-abcd",
                            "pizza": "Margherita",
                            "status": "NEW",
                            "createdAt": "2025-10-28T22:00:00",
                            "updatedAt": "2025-10-28T22:00:00"
                        }
                        """)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Order not found for the given code"
                )
            }
    )
    public ResponseEntity<PurchaseDto> checkStatus(
            @Parameter(description = "Unique order code", required = true, example = "uuid-1234-abcd")
            @NotBlank(message = "Order code is required")
            @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Order code must contain only letters, numbers and hyphens")
            @Size(min = 8, max = 50, message = "Order code must be between 8 and 50 characters")
            @PathVariable String code) {
        logger.debug("checkStatus controller called with code={}", code);
        PurchaseDto purchase = purchaseService.checkPurchaseStatusByCode(code);
        logger.debug("checkStatus controller returning={}", purchase);
        return ResponseEntity.ok(purchase);
    }
}
