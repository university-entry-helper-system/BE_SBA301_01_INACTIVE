package org.example.sba.command;

import lombok.Data;

@Data
public class ChangePasswordCommand {
    private String username;
    private String oldPassword;
    private String newPassword;
} 