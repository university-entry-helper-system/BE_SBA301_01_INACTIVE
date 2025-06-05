package org.example.sba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.example.sba.util.AccountStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.example.sba.util.Gender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_account")
public class Account extends AbstractEntity<Long> implements UserDetails, Serializable {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<AccountHasRole> roles = new HashSet<>();

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "avatar", nullable = true)
    private String avatar;

    @Column(name = "code", nullable = true)
    private String code;

    @Column(name = "created_date")
    private Instant createdDate;

    @Size(max = 50)
    @Column(name = "created_by",  length = 50)
    private String createdBy;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @Size(max = 50)
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

