package org.example.sba.modules.auth;

import lombok.Getter;

@Getter
public class ForgotPasswordCommand {
    private final String email;

    public ForgotPasswordCommand(String email) {
        this.email = email;
    }
} 