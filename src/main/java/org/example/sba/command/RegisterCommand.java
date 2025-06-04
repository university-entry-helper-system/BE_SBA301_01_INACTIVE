package org.example.sba.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.sba.util.Gender;
import org.example.sba.validation.PasswordMatch;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Data
@PasswordMatch(message = "Password and confirm password must match")
public class RegisterCommand {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be one of: MALE, FEMALE, OTHER")
    private String gender;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=\\S+$).{8,}$", 
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and no whitespace")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
} 