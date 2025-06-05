package org.example.sba.modules.auth;

import lombok.Getter;
import org.example.sba.dto.request.ResetPasswordDTO;

@Getter
public class ResetPasswordCommand {
    private final String resetToken;
    private final String newPassword;
    private final String confirmPassword;

    public ResetPasswordCommand(ResetPasswordDTO dto) {
        this.resetToken = dto.getResetToken();
        this.newPassword = dto.getNewPassword();
        this.confirmPassword = dto.getConfirmPassword();
    }
} 