package org.example.sba.modules.auth;

import lombok.Data;

@Data
public class RefreshTokenCommand {
    private String refreshToken;
} 