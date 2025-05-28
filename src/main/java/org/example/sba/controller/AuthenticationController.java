package org.example.sba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.ResponseData;
import org.example.sba.dto.response.ResponseError;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.service.AuthenticationService;
import org.springframework.http.HttpStatus;

@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/access-token")
    public ResponseEntity<ResponseData<TokenResponse>> accessToken(@Valid @RequestBody SignInRequest request) {
        try {
            TokenResponse tokenResponse = authenticationService.accessToken(request);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Login successful", tokenResponse),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Login failed"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenResponse>> refreshToken(HttpServletRequest request) {
        try {
            TokenResponse tokenResponse = authenticationService.refreshToken(request);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Token refreshed", tokenResponse),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Token refresh failed"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/remove-token")
    public ResponseEntity<ResponseData<String>> removeToken(HttpServletRequest request) {
        try {
            String result = authenticationService.removeToken(request);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Token removed", result),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Token removal failed"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseData<String>> forgotPassword(@RequestParam String email) {
        try {
            String result = authenticationService.forgotPassword(email);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Password reset email sent", result),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Failed to send password reset email"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<String>> resetPassword(@RequestParam String secretKey) {
        try {
            String result = authenticationService.resetPassword(secretKey);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Password reset successful", result),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Password reset failed"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseData<String>> changePassword(@Valid @RequestBody ResetPasswordDTO request) {
        try {
            String result = authenticationService.changePassword(request);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Password changed successfully", result),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Password change failed"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request) {
        try {
            String result = authenticationService.logout(request);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Logout successful", result),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseEntity<>(
                    new ResponseError(HttpStatus.BAD_REQUEST.value(), "Logout failed"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}