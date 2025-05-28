package org.example.sba.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.example.sba.util.TokenType;

public interface JwtService {

    String generateToken(UserDetails account);

    String generateRefreshToken(UserDetails account);

    String generateResetToken(UserDetails account);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails account);
}