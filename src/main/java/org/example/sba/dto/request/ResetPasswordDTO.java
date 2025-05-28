package org.example.sba.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @NotBlank(message = "secretKey must be not blank")
    private String secretKey;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotBlank(message = "confirmPassword must be not blank")
    private String confirmPassword;

}