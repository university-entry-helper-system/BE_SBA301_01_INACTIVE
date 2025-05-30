package org.example.sba.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.sba.util.Platform;

import java.io.Serializable;

@Data
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must not be blank")
    private String username;

    @NotBlank(message = "password must not be blank")
    private String password;

    @NotNull(message = "platform must not be blank")
    private Platform platform;

    private String deviceToken;

    private String version;
}