package org.example.sba.service;

public interface RedisTokenService {
    void saveActivationToken(String token, Long accountId, int expirationInSeconds);
    Long getAccountIdFromActivationToken(String token);
}
