package org.example.sba.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.sba.model.Role;
import org.example.sba.service.AccountService;
import org.example.sba.service.AuthenticationService;
import org.example.sba.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.example.sba.service.TokenService;
import org.springframework.stereotype.Service;
import org.example.sba.dto.request.SignInRequest;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.exception.InvalidDataException;
import org.example.sba.model.Token;

import java.util.List;

import static org.springframework.http.HttpHeaders.REFERER;
import static org.example.sba.util.TokenType.ACCESS_TOKEN;
import static org.example.sba.util.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;
    private final JwtService jwtService;

    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var account = accountService.getByUsername(signInRequest.getUsername());

        List<Role> roles = accountService.findRolesByAccountId(account.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();


        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities));

        String accessToken = jwtService.generateToken(account);

        String refreshToken = jwtService.generateRefreshToken(account);

        // save token to db
        tokenService.saveToken(Token.builder().username(account.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

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

        // create new access token
        String accessToken = jwtService.generateToken(account);

        // save token to db
        tokenService.saveToken(Token.builder().username(account.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .build();
    }

    @Override
    public String logout(HttpServletRequest request) {
        log.info("---------- logout ----------");

        final String token = request.getHeader(REFERER);
        if (StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);
        tokenService.deleteTokenByUsername(userName);

        return "Deleted!";
    }
}
