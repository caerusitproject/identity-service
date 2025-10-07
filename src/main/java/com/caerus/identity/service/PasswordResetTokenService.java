package com.caerus.identity.service;


import com.caerus.identity.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final String TOKEN_PREFIX = "reset-token:";

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String email, String token, Duration ttl) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, ttl);
    }

    public String validateToken(String token) {
        String key = TOKEN_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);
        if (email == null) {
            throw new BadRequestException("Invalid or expired token");
        }
        return email;
    }

    public void invalidateToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }
}