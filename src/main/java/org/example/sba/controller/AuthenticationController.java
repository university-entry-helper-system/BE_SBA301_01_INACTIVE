package org.example.sba.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.modules.auth.SignInCommand;
import org.example.sba.modules.auth.RegisterCommand;
import org.example.sba.dto.request.ChangePasswordDTO;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.ResponseData;
import org.example.sba.dto.response.ResponseError;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;
import org.example.sba.service.AuthenticationService;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.example.sba.repository.AccountRepository;
import org.example.sba.util.AccountStatus;
import org.example.sba.util.TokenType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "APIs for user authentication and authorization")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final RedisTokenService redisTokenService;

    @Operation(
        summary = "Register new user",
        description = "Register a new user account. The user will receive a confirmation email."
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        try {
            Account account = authenticationService.register(command);
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
        description = "Authenticate user and return JWT access and refresh tokens."
    )
    @PostMapping("/access-token")
    public ResponseEntity<?> accessToken(@Valid @RequestBody SignInRequest request) {
        try {
            SignInCommand command = new SignInCommand();
            command.setUsername(request.getUsername());
            command.setPassword(request.getPassword());
            TokenResponse tokenResponse = authenticationService.login(command);
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Login successful", tokenResponse));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return ResponseEntity.badRequest().body(new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Login failed", null));
        }
    }

    @Operation(
        summary = "Change password",
        description = "Change the password for a logged-in user. Requires old and new passwords."
    )
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO request) {
        try {
            String result = authenticationService.changePassword(request);
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result, null));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return ResponseEntity.badRequest().body(new ResponseError(HttpStatus.BAD_REQUEST.value(), "Password change failed: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "User logout",
        description = "Logout the user and invalidate their tokens. Requires a valid access token."
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
            if (username == null) throw new RuntimeException("Cannot resolve username from request");
            String result = authenticationService.logout(username);
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result, null));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return ResponseEntity.badRequest().body(new ResponseError(HttpStatus.BAD_REQUEST.value(), "Logout failed: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Forgot password",
        description = "Send a password reset link to the user's email if the account exists."
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        log.info("Received forgot password request for email: {}", email);
        try {
            String result = authenticationService.forgotPassword(email);
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result, null));
        } catch (Exception e) {
            log.error("Forgot password failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseError(HttpStatus.BAD_REQUEST.value(), "Forgot password failed: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Reset password",
        description = "Reset the user's password using a token received via email."
    )
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        try {
            String result = authenticationService.resetPassword(request);
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), result, null));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return ResponseEntity.badRequest().body(new ResponseError(HttpStatus.BAD_REQUEST.value(), "Password reset failed: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Activate account",
            description = "Activate a user account using the activation token sent to their email."
    )
    @GetMapping("/activate")
    public ResponseEntity<ResponseData> activateAccount(@RequestParam String token) {
        log.info("Received account activation request with token: {}", token);
        try {
            log.info("Extracting username from token...");
            String username = jwtService.extractUsername(token, TokenType.ACTIVATION_TOKEN);
            log.info("Username extracted: {}", username);
            
            log.info("Looking up account in database...");
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            log.info("Account found: {}", account.getUsername());
            
            log.info("Checking if token exists in Redis...");
            Long accountId = redisTokenService.getAccountIdFromActivationToken(token);
            if (accountId == null) {
                log.error("Token not found in Redis");
                return ResponseEntity.badRequest().body(new ResponseData<>(400, "Activation failed: Invalid or expired token", null));
            }
            log.info("Token found in Redis for account ID: {}", accountId);
            
            if (!accountId.equals(account.getId())) {
                log.error("Token account ID mismatch. Token account ID: {}, Found account ID: {}", accountId, account.getId());
                return ResponseEntity.badRequest().body(new ResponseData<>(400, "Activation failed: Token account mismatch", null));
            }
            
            log.info("Setting account status to ACTIVE...");
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
            log.info("Account activated successfully");
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("username", account.getUsername());
            responseData.put("email", account.getEmail());
            responseData.put("status", account.getStatus());
            
            return ResponseEntity.ok(new ResponseData<>(200, "Account activated successfully", responseData));
        } catch (Exception e) {
            log.error("Activation failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseData<>(400, "Activation failed: " + e.getMessage(), null));
        }
    }
}