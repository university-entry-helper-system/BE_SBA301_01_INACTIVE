package org.example.sba.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.sba.util.Gender;
import org.example.sba.util.AccountStatus;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;

@Builder
@Getter
public class AccountDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private AccountStatus status;

    private String avatar;

    private String code;

    Instant createDate;
    String createdBy;
    Instant updateDate;
    String updatedBy;
}
