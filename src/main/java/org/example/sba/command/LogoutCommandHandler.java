package org.example.sba.command;

import lombok.RequiredArgsConstructor;
import org.example.sba.repository.RedisTokenRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutCommandHandler {
    private final RedisTokenRepository redisTokenRepository;

    public void handle(LogoutCommand command) {
        redisTokenRepository.deleteById(command.getUsername());
    }
} 