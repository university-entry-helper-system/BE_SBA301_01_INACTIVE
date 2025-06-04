package org.example.sba.query;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.RedisToken;
import org.example.sba.repository.RedisTokenRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenStatusQueryHandler {
    private final RedisTokenRepository redisTokenRepository;

    public RedisToken handle(TokenStatusQuery query) {
        return redisTokenRepository.findById(query.getUsername())
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }
} 