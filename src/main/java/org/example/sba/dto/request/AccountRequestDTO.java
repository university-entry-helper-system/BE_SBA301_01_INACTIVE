package org.example.sba.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.sba.util.CustomDateDeserializer;
import org.example.sba.util.Gender;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "First name must not be blank")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd") // Ensure the date format is correctly parsed
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date dateOfBirth;

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{9,12}$", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
