package org.example.sba.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.sba.command.*;
import org.example.sba.query.*;
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
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final LogoutCommandHandler logoutCommandHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;
    private final AccountInfoQueryHandler accountInfoQueryHandler;
    private final TokenStatusQueryHandler tokenStatusQueryHandler;

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setUsername(request.getUsername());
        command.setOldPassword(request.getOldPassword());
        command.setNewPassword(request.getNewPassword());
        changePasswordCommandHandler.handle(command);
        return "Changed";
    }

    @Override
    public String logout(HttpServletRequest request) {
        log.info("---------- logout ----------");

        LogoutCommand command = new LogoutCommand();
        command.setUsername(request.getHeader("username"));
        logoutCommandHandler.handle(command);
        return "Logout";
    }

    @Override
    public String resetPassword(String secretKey) {
        log.info("---------- resetPassword ----------");

        ResetPasswordCommand command = new ResetPasswordCommand();
        command.setUsername(secretKey);
        command.setResetToken(secretKey);
        command.setNewPassword("newPassword");
        resetPasswordCommandHandler.handle(command);
        return "Reset";
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
