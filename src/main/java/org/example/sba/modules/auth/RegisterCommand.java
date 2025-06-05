package org.example.sba.modules.auth;

import lombok.Data;
import org.example.sba.util.Gender;
import jakarta.persistence.Column;
import java.sql.Date;

@Data
public class RegisterCommand {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Gender gender;
    private String avatar;
    private Date dateOfBirth;
} 