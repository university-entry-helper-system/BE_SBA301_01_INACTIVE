package org.example.sba.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.model.Account;
import org.example.sba.model.AccountHasRole;
import org.example.sba.model.Role;
import org.example.sba.repository.AccountHasRoleRepository;
import org.example.sba.repository.AccountRepository;
import org.example.sba.repository.RoleRepository;
import org.example.sba.service.EmailService;
import org.example.sba.service.RedisTokenService;
import org.example.sba.util.AccountStatus;
import org.example.sba.util.Gender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterCommandHandler {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountHasRoleRepository accountHasRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisTokenService redisTokenService;

    @Transactional
    public Account handle(RegisterCommand command, BindingResult bindingResult) {
        log.info("Starting registration process for username: {}", command.getUsername());

        // Log validation errors if any
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());
            log.error("Validation errors: {}", errors);
            throw new RuntimeException("Validation failed: " + String.join(", ", errors));
        }

        // Validate password match
        if (!command.getPassword().equals(command.getConfirmPassword())) {
            log.error("Password mismatch for username: {}", command.getUsername());
            throw new RuntimeException("Passwords do not match");
        }

        // Check if username exists
        if (accountRepository.findByUsername(command.getUsername()).isPresent()) {
            log.error("Username already exists: {}", command.getUsername());
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (accountRepository.findByEmail(command.getEmail()).isPresent()) {
            log.error("Email already exists: {}", command.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Check if phone exists
        if (accountRepository.findByPhone(command.getPhone()).isPresent()) {
            log.error("Phone number already exists: {}", command.getPhone());
            throw new RuntimeException("Phone number already exists");
        }

        log.info("Creating new account for username: {}", command.getUsername());

        // Generate activation token
        String activationToken = UUID.randomUUID().toString();
        
        // Create new account
        Account account = Account.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .dateOfBirth(command.getDateOfBirth())
                .gender(Gender.valueOf(command.getGender()))
                .phone(command.getPhone())
                .email(command.getEmail())
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .status(AccountStatus.INACTIVE)
                .createdDate(Instant.now())
                .build();

        // Save account
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());

        // Get role with id = 2 (USER role)
        Role userRole = roleRepository.findById(2)
                .orElseThrow(() -> {
                    log.error("Default role (id=2) not found");
                    return new RuntimeException("Default role not found");
                });

        // Create account-role relationship
        AccountHasRole accountHasRole = AccountHasRole.builder()
                .account(savedAccount)
                .role(userRole)
                .build();

        // Save account-role relationship
        accountHasRoleRepository.save(accountHasRole);
        log.info("User role assigned successfully for account ID: {}", savedAccount.getId());

        // Save activation token to Redis with 24 hours expiration
        redisTokenService.saveActivationToken(activationToken, savedAccount.getId(), 24 * 60 * 60);

        // Send activation email
        try {
            emailService.sendActivationEmail(savedAccount.getEmail(), activationToken);
            log.info("Activation email sent successfully to: {}", savedAccount.getEmail());
        } catch (Exception e) {
            log.error("Failed to send activation email: {}", e.getMessage());
            // Don't throw exception here to allow registration to complete
        }

        return savedAccount;
    }
} 