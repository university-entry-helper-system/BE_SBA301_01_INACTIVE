package org.example.sba.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;

public interface AuthenticationService {

    TokenResponse accessToken(SignInRequest signInRequest);

    TokenResponse refreshToken(HttpServletRequest request);

    String removeToken(HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(String secretKey);

    String changePassword(ResetPasswordDTO request);
}
