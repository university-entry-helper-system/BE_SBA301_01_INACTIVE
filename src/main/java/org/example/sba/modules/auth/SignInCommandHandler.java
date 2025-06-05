package org.example.sba.modules.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;
import org.example.sba.model.Role;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.example.sba.modules.auth.SignInCommand;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInCommandHandler {
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse handle(SignInCommand command) {
        var account = accountRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: username {} not found", command.getUsername());
                    return new RuntimeException("Invalid username or password");
                });

        if (!account.isEnabled()) {
            log.warn("Login failed: account {} is not active", command.getUsername());
            throw new RuntimeException("Account not active");
        }

        if (!passwordEncoder.matches(command.getPassword(), account.getPassword())) {
            log.warn("Login failed: wrong password for username {}", command.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        List<Role> roles = accountRepository.findRolesByAccountId(account.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                command.getUsername(),
                command.getPassword(),
                authorities));

        String accessToken = jwtService.generateToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        redisTokenService.saveActivationToken(accessToken, account.getId(), 24 * 60 * 60);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .roles(roles.stream().map(Role::getName).toList())
                .build();
    }
} 