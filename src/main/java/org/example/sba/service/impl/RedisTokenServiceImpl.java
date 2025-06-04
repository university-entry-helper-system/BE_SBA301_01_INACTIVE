package org.example.sba.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.sba.service.RedisTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACTIVATION_TOKEN_PREFIX = "activation_token:";

    @Override
    public void saveActivationToken(String token, Long accountId, int expirationInSeconds) {
        String key = ACTIVATION_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, accountId.toString(), expirationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Long getAccountIdFromActivationToken(String token) {
        String key = ACTIVATION_TOKEN_PREFIX + token;
        String accountId = redisTemplate.opsForValue().get(key);
        if (accountId != null) {
            redisTemplate.delete(key); // Delete token after use
            return Long.parseLong(accountId);
        }
        return null;
    }

    @Override
    public void remove(String token) {
        String key = ACTIVATION_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    @Override
    public boolean isExists(String token) {
        String key = ACTIVATION_TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
