package org.example.sba.command;

import lombok.Data;

@Data
public class ResetPasswordCommand {
    private String username;
    private String resetToken;
    private String newPassword;
} 