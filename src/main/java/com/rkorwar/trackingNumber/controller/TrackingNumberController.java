package com.rkorwar.trackingNumber.controller;

import com.rkorwar.trackingNumber.model.TrackingNumberResponse;
import com.rkorwar.trackingNumber.service.TrackingNumberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RestController
@AllArgsConstructor
public class TrackingNumberController {

    private TrackingNumberService trackingNumberService;

    @GetMapping("/next-tracking-number")
    public ResponseEntity<?> getNextTrackingNumber(
                                @RequestParam String origin_country_id,
                                @RequestParam String destination_country_id,
                                @RequestParam double weight,
                                @RequestParam String created_at,
                                @RequestParam String customer_id,
                                @RequestParam String customer_name,
                                @RequestParam String customer_slug) {

        String trackingNumber = trackingNumberService.generateTrackingNumber(
                origin_country_id, destination_country_id, weight, created_at, customer_id);
        String createdAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        return ResponseEntity.ok(new TrackingNumberResponse(trackingNumber, createdAt));
    }
}
