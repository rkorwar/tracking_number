package com.rkorwar.trackingNumber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

@Service
public class TrackingNumberService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Pattern TRACKING_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{1,16}$");

    public String generateTrackingNumber(String originCountryId,
                                         String destinationCountryId,
                                         double weight,
                                         String createdAt,
                                         String customerId) {

        Long counter = redisTemplate.opsForValue().increment("tracking_number_counter", 1);
        String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        String trackingNumber = String.format("%s%s%04d%s",
                originCountryId.toUpperCase(), destinationCountryId.toUpperCase(),
                counter % 10000, uuidPart);

        if (trackingNumber.length() > 16) {
            trackingNumber = trackingNumber.substring(0, 16);
        }

        if (!TRACKING_NUMBER_PATTERN.matcher(trackingNumber).matches()) {
            throw new IllegalArgumentException("Generated tracking number does not match the required pattern.");
        }

        return trackingNumber;
    }
}
