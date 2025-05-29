package org.example.sba.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.sba.dto.validator.EnumPattern;
import org.example.sba.dto.validator.PhoneNumber;
import org.example.sba.util.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO implements Serializable {

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 50, message = "First name must be between 50 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 50 characters")
    private String lastName;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @NotNull(message = "Gender must not be null")
    @EnumPattern(name = "gender", regexp = "MALE|FEMALE|OTHER")
    private Gender gender;

    @PhoneNumber
    private String phone;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String password;

    String avatar;
    String code;

    Instant createDate;
    String createdBy;
    Instant updateDate;
    String updatedBy;
}
