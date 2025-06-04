package org.example.sba.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.example.sba.command.*;
import org.example.sba.query.*;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.ResponseData;
import org.example.sba.dto.response.ResponseError;
import org.example.sba.dto.response.TokenResponse;
import org.springframework.http.HttpStatus;
import org.example.sba.model.Account;
import org.example.sba.service.RedisTokenService;
import org.example.sba.repository.AccountRepository;
import org.example.sba.util.AccountStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "APIs for user authentication and authorization")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SignInCommandHandler signInCommandHandler;
    private final RegisterCommandHandler registerCommandHandler;
    private final RefreshTokenCommandHandler refreshTokenCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final LogoutCommandHandler logoutCommandHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;
    private final AccountInfoQueryHandler accountInfoQueryHandler;
    private final TokenStatusQueryHandler tokenStatusQueryHandler;
    private final RedisTokenService redisTokenService;
    private final AccountRepository accountRepository;

    @Operation(
        summary = "Register new user",
        description = "Register a new user account with validation. The user will receive a confirmation email."
    )
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterCommand command, BindingResult bindingResult) {
        log.info("Received registration request for username: {}", command.getUsername());
        try {
            Account account = registerCommandHandler.handle(command, bindingResult);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Registration successful");
            response.put("data", account);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT access token and refresh token. The access token is valid for 24 hours."
    )
    @PostMapping("/access-token")
    public ResponseEntity<ResponseData<TokenResponse>> accessToken(@Valid @RequestBody SignInRequest request) {
        try {
            SignInCommand command = new SignInCommand();
            command.setUsername(request.getUsername());
            command.setPassword(request.getPassword());
            TokenResponse tokenResponse = signInCommandHandler.handle(command);
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

    @Operation(
        summary = "Refresh access token",
        description = "Generate a new access token using a valid refresh token. The refresh token is valid for 7 days."
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenResponse>> refreshToken(@RequestHeader("refresh-token") String refreshToken) {
        try {
            RefreshTokenCommand command = new RefreshTokenCommand();
            command.setRefreshToken(refreshToken);
            TokenResponse tokenResponse = refreshTokenCommandHandler.handle(command);
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

    @Operation(
        summary = "Change password",
        description = "Change user password. Requires old password for verification and new password must meet security requirements."
    )
    @PostMapping("/change-password")
    public ResponseEntity<ResponseData<String>> changePassword(@Valid @RequestBody ResetPasswordDTO request) {
        try {
            ChangePasswordCommand command = new ChangePasswordCommand();
            command.setUsername(request.getUsername());
            command.setOldPassword(request.getOldPassword());
            command.setNewPassword(request.getNewPassword());
            changePasswordCommandHandler.handle(command);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Password changed successfully", "Changed"),
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

    @Operation(
        summary = "User logout",
        description = "Logout user and invalidate their tokens. Requires valid access token in Authorization header."
    )
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request) {
        try {
            LogoutCommand command = new LogoutCommand();
            command.setUsername(request.getHeader("username"));
            logoutCommandHandler.handle(command);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Logout successful", "Logout"),
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

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<String>> resetPassword(@RequestParam String secretKey) {
        try {
            ResetPasswordCommand command = new ResetPasswordCommand();
            command.setUsername(secretKey);
            command.setResetToken(secretKey);
            command.setNewPassword("newPassword");
            resetPasswordCommandHandler.handle(command);
            return new ResponseEntity<>(
                    new ResponseData<>(HttpStatus.OK.value(), "Password reset successful", "Reset"),
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

    @GetMapping("/activate")
    public ResponseEntity<Map<String, Object>> activateAccount(@RequestParam String token) {
        log.info("Received account activation request with token: {}", token);
        try {
            Long accountId = redisTokenService.getAccountIdFromActivationToken(token);
            if (accountId == null) {
                log.error("Invalid or expired activation token");
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("message", "Invalid or expired activation token");
                return ResponseEntity.badRequest().body(response);
            }

            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);

            log.info("Account activated successfully: {}", account.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Account activated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Account activation failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "Account activation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}