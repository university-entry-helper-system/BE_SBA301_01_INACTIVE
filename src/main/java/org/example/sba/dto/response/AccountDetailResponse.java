package org.example.sba.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import org.example.sba.util.CustomDateDeserializer;
import org.example.sba.util.Gender;
import org.example.sba.util.AccountStatus;

import java.io.Serializable;
import java.sql.Date;

@Builder
@Getter
public class AccountDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private AccountStatus status;
}
