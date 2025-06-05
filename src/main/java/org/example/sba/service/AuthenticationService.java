package org.example.sba.service;

import org.example.sba.modules.auth.SignInCommand;
import org.example.sba.modules.auth.RegisterCommand;
import org.example.sba.dto.request.ChangePasswordDTO;
import org.example.sba.dto.request.ResetPasswordDTO;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;

public interface AuthenticationService {
    Account register(RegisterCommand command);
    TokenResponse login(SignInCommand command);
    String changePassword(ChangePasswordDTO request);
    String forgotPassword(String email);
    String resetPassword(ResetPasswordDTO request);
    String logout(String username);
}
