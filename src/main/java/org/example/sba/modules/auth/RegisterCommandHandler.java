package org.example.sba.modules.auth;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.*;
import org.example.sba.repository.AccountRepository;
import org.example.sba.repository.RoleRepository;
import org.example.sba.repository.AccountHasRoleRepository;
import org.example.sba.service.AccountSyncService;
import org.example.sba.service.EmailService;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.example.sba.util.AccountStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RegisterCommandHandler {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountSyncService accountSyncService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final AccountHasRoleRepository accountHasRoleRepository;

    public Account handle(RegisterCommand command) {
        // Validate unique username, email, phone
        if (accountRepository.findByUsername(command.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (accountRepository.findByEmail(command.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if (StringUtils.hasText(command.getPhone()) && accountRepository.findByPhone(command.getPhone()).isPresent()) {
            throw new RuntimeException("Phone already exists");
        }

        // Validate email format
        if (!command.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format");
        }
        // Validate phone format (simple VN phone regex, customize as needed)
        if (StringUtils.hasText(command.getPhone()) && !Pattern.matches("^(0|\\+84)[0-9]{9,10}$", command.getPhone())) {
            throw new RuntimeException("Invalid phone number");
        }
        // Validate password strength
        if (!isStrongPassword(command.getPassword())) {
            throw new RuntimeException("Password must be at least 8 characters, include uppercase, lowercase, number and special character");
        }

        // Set role (id=2, USER)
        Role userRole = roleRepository.findById(2).orElseThrow(() -> new RuntimeException("Default user role not found"));
        Account account = Account.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .phone(command.getPhone())
                .status(AccountStatus.INACTIVE)
                .gender(command.getGender())
                .dateOfBirth(command.getDateOfBirth())
                .avatar(command.getAvatar())
                .roles(new HashSet<>())
                .build();
        Account saved = accountRepository.save(account);
        
        // Gán role cho account
        AccountHasRole accountHasRole = AccountHasRole.builder().account(saved).role(userRole).build();
        accountHasRoleRepository.save(accountHasRole);
        accountRepository.save(saved);
        
        accountSyncService.syncToMongo(saved);

        // Sinh token active, lưu Redis, gửi email
        String activationToken = jwtService.generateActivationToken(saved);
        redisTokenService.saveActivationToken(activationToken, saved.getId(), 24 * 60 * 60); // 24h
        emailService.sendActivationEmail(saved.getEmail(), activationToken);

        return saved;
    }

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
} 