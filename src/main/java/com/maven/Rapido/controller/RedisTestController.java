package com.maven.Rapido.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class RedisTestController {
    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/redis-test")
    public ResponseEntity<String> testRedis() {
        try {
            redisTemplate.opsForValue().set("ping", "pong");
            String value = redisTemplate.opsForValue().get("ping");
            return ResponseEntity.ok("Redis is connected. Value: " + value);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Redis connection failed: " + e.getMessage());
        }
    }
}
