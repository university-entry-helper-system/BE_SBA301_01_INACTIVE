package org.example.sba.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.modules.auth.*;
import org.example.sba.modules.auth.RegisterCommandHandler;
import org.example.sba.modules.auth.ChangePasswordCommandHandler;
import org.example.sba.modules.auth.ForgotPasswordCommandHandler;
import org.example.sba.modules.auth.ResetPasswordCommandHandler;
import org.example.sba.modules.auth.LogoutCommandHandler;
import org.example.sba.modules.auth.RegisterCommand;
import org.example.sba.modules.auth.ChangePasswordCommand;
import org.example.sba.modules.auth.ForgotPasswordCommand;
import org.example.sba.modules.auth.ResetPasswordCommand;
import org.example.sba.modules.auth.LogoutCommand;
import org.example.sba.dto.request.ChangePasswordDTO;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;
import org.example.sba.service.AuthenticationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RegisterCommandHandler registerCommandHandler;
    private final SignInCommandHandler signInCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final ForgotPasswordCommandHandler forgotPasswordCommandHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;
    private final LogoutCommandHandler logoutCommandHandler;

    @Override
    public Account register(RegisterCommand command) {
        return registerCommandHandler.handle(command);
    }

    @Override
    public TokenResponse login(SignInCommand command) {
        return signInCommandHandler.handle(command);
    }

    @Override
    public String changePassword(ChangePasswordDTO request) {
        return changePasswordCommandHandler.handle(new ChangePasswordCommand(request));
    }

    @Override
    public String forgotPassword(String email) {
        return forgotPasswordCommandHandler.handle(new ForgotPasswordCommand(email));
    }

    @Override
    public String resetPassword(ResetPasswordDTO request) {
        return resetPasswordCommandHandler.handle(new ResetPasswordCommand(request));
    }

    @Override
    public String logout(String username) {
        return logoutCommandHandler.handle(new LogoutCommand(username));
    }
}
