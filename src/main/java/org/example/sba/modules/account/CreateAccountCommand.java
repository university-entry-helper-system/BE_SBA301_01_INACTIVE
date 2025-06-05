package org.example.sba.modules.account;

import lombok.Data;

@Data
public class CreateAccountCommand {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    // ... các trường khác cần thiết
} 