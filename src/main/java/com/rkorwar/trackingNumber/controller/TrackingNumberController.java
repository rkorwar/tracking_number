package com.rkorwar.trackingNumber.controller;

import com.rkorwar.trackingNumber.model.TrackingNumberResponse;
import com.rkorwar.trackingNumber.service.TrackingNumberService;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
@Validated
public class TrackingNumberController {

    private TrackingNumberService trackingNumberService;

    @GetMapping("/next-tracking-number")
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
}
