package org.example.sba.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.model.Account;
import org.example.sba.model.RedisToken;
import org.example.sba.model.Role;
import org.example.sba.service.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.exception.InvalidDataException;

import java.util.List;

import static org.example.sba.util.TokenType.*;
import static org.springframework.http.HttpHeaders.REFERER;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse accessToken(SignInRequest signInRequest) {
        log.info("---------- accessToken ----------");

        var account = accountService.getByUsername(signInRequest.getUsername());
        if (!account.isEnabled()) {
            throw new InvalidDataException("Account not active");
        }

        List<Role> roles = accountService.findRolesByAccountId(account.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities));

        String accessToken = jwtService.generateToken(account);

        String refreshToken = jwtService.generateRefreshToken(account);

        redisTokenService.save(RedisToken.builder().id(account.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .build();
    }

    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        final String refreshToken = request.getHeader(REFERER);
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
        var account = accountService.getByUsername(userName);
        if (!jwtService.isValid(refreshToken, REFRESH_TOKEN, account)) {
            throw new InvalidDataException("Not allow access with this token");
        }

        String accessToken = jwtService.generateToken(account);

        redisTokenService.save(RedisToken.builder().id(account.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .build();
    }

    public String removeToken(HttpServletRequest request) {
        log.info("---------- removeToken ----------");

        final String token = request.getHeader(REFERER);
        if (StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);

        redisTokenService.remove(userName);

        return "Removed!";
    }

    public String forgotPassword(String email) {
        log.info("---------- forgotPassword ----------");

        Account account = accountService.getAccountByEmail(email);

        String resetToken = jwtService.generateResetToken(account);

        redisTokenService.save(RedisToken.builder().id(account.getUsername()).resetToken(resetToken).build());

        // TODO send email to account
        String confirmLink = String.format("curl --location 'http://localhost:80/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", resetToken);
        log.info("--> confirmLink: {}", confirmLink);

        return resetToken;
    }

    public String resetPassword(String secretKey) {
        log.info("---------- resetPassword ----------");

        var account = validateToken(secretKey);

        // check token by username
        tokenService.getTokenByUsername(account.getUsername());

        return "Reset";
    }

    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }

        var account = validateToken(request.getSecretKey());

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountService.saveAccount(account);

        return "Changed";
    }

    private Account validateToken(String token) {
        var userName = jwtService.extractUsername(token, RESET_TOKEN);

        redisTokenService.isExists(userName);

        var account = accountService.getByUsername(userName);
        if (!account.isEnabled()) {
            throw new InvalidDataException("Account not active");
        }

        return account;
    }
}