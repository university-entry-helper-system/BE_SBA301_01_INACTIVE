package org.example.sba.modules.auth;

import lombok.Getter;
import org.example.sba.dto.request.ChangePasswordDTO;

@Getter
public class ChangePasswordCommand {
    private final String username;
    private final String oldPassword;
    private final String newPassword;
    private final String confirmPassword;

    public ChangePasswordCommand(ChangePasswordDTO dto) {
        this.username = dto.getUsername();
        this.oldPassword = dto.getOldPassword();
        this.newPassword = dto.getNewPassword();
        this.confirmPassword = dto.getConfirmPassword();
    }
} 