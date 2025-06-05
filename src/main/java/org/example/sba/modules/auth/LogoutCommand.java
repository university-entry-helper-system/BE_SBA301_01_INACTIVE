package org.example.sba.modules.auth;

import lombok.Getter;

@Getter
public class LogoutCommand {
    private final String username;

    public LogoutCommand(String username) {
        this.username = username;
    }
} 