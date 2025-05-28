package org.example.sba.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse authenticate(SignInRequest signInRequest);

    TokenResponse refreshToken(HttpServletRequest request);

    String logout(HttpServletRequest request);
}
