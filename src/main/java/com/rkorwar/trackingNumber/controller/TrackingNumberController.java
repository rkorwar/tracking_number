package com.rkorwar.trackingNumber.controller;

import com.rkorwar.trackingNumber.model.TrackingNumberResponse;
import com.rkorwar.trackingNumber.service.TrackingNumberService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(
        name = "Tracking Number API",
        description = "API to manage Tracking Number"
)
@RestController
@AllArgsConstructor
@Validated
public class TrackingNumberController {

    private TrackingNumberService trackingNumberService;

    @GetMapping("/next-tracking-number")
    @Operation(
            summary = "Generate Next Tracking Number",
            description = "The API generates the next tracking number based on the provided request parameters.",
            parameters = {
                    @Parameter(name = "origin_country_id", description = "Origin country code (ISO 3166-1 alpha-2 format)", required = true),
                    @Parameter(name = "destination_country_id", description = "Destination country code (ISO 3166-1 alpha-2 format)", required = true),
                    @Parameter(name = "weight", description = "Weight of the shipment (positive number, up to 3 decimals)", required = true),
                    @Parameter(name = "created_at", description = "Timestamp of the shipment creation (RFC 3339 format)", required = true),
                    @Parameter(name = "customer_id", description = "Unique customer identifier (UUID format)", required = true),
                    @Parameter(name = "customer_name", description = "Customer's name", required = true),
                    @Parameter(name = "customer_slug", description = "The customer’s name in slug-case/kebab-case (ex: redbox-logistics)", required = true)
            }
    )
    public ResponseEntity<?> getNextTrackingNumber(
            @RequestParam @NotNull(message = "origin_country_id is a required parameter")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid origin_country_id format. Must be ISO 3166-1 alpha-2 format.")
            String origin_country_id,

            @RequestParam @NotNull(message = "destination_country_id is a required parameter")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid destination_country_id format. Must be ISO 3166-1 alpha-2 format.")
            String destination_country_id,

            @RequestParam @NotNull(message = "weight is a required parameter")
            @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be a positive number.")
            @Digits(integer = 3, fraction = 3, message = "Weight must have up to three decimal places.")
            Double weight,

            @RequestParam @NotNull(message = "created_at is a required parameter")
            @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*)$", message = "Invalid date format. Must be in RFC 3339 format.")
            String created_at,

            @RequestParam @NotNull(message = "customer_id is a required parameter")
            String customer_id,

            @RequestParam @NotBlank(message = "customer_name cannot be empty")
            String customer_name,

            @RequestParam @NotBlank(message = "customer_slug cannot be empty")
            String customer_slug) {

        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin_country_id, destination_country_id, weight, created_at, customer_id);
        String createdAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        return ResponseEntity.ok(new TrackingNumberResponse(trackingNumber, createdAt));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");

        // Get the first error message (or the first violation)
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        response.put("message", fieldError.getDefaultMessage());
        response.put("path", "/next-tracking-number");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Required parameter '" + ex.getParameterName() + "' is not present.");
        response.put("path", "/next-tracking-number");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");

        // Iterate through all violations and add them to the response
        ex.getConstraintViolations().forEach(violation -> {
            String message = violation.getMessage();
            response.put("message", message); // You can also collect all messages or choose the first one
        });

        response.put("path", "/next-tracking-number");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
