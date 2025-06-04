package org.example.sba.command;

import lombok.Data;

@Data
public class SignInCommand {
    private String username;
    private String password;
    // Thêm các trường khác nếu cần
} 