package org.example.sba.service;

import org.example.sba.model.Account;
import org.example.sba.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateResetToken(UserDetails userDetails);

    String generateActivationToken(UserDetails userDetails);

    String extractUsername(String token, TokenType tokenType);

    boolean isValid(String token, TokenType tokenType, UserDetails userDetails);
}