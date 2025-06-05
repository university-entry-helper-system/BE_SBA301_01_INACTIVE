package org.example.sba.modules.auth;

import lombok.Data;

@Data
public class SignInCommand {
    private String username;
    private String password;
} 