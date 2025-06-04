package org.example.sba.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @NotBlank(message = "secretKey must not be blank")
    private String secretKey;

    @NotBlank(message = "password must not be blank")
    private String password;

    @NotBlank(message = "confirmPassword must not be blank")
    private String confirmPassword;

    private String username;
    private String oldPassword;
    private String newPassword;

}