package org.example.sba.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.sba.dto.validator.PasswordMatch;

@Data
@PasswordMatch(message = "New password and confirm password must match")
public class ChangePasswordDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and no whitespace")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
} 