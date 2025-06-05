package org.example.sba.modules.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.service.RedisTokenService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutCommandHandler {
    private final RedisTokenService redisTokenService;

    public String handle(LogoutCommand command) {
        log.info("Processing logout request for user: {}", command.getUsername());
        
        redisTokenService.remove(command.getUsername());
        
        log.info("Logout successful for user: {}", command.getUsername());
        return "Logged out successfully";
    }
} 