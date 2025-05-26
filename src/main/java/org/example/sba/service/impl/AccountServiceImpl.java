package org.example.sba.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.model.Account;
import org.example.sba.model.Role;
import org.example.sba.repository.AccountRepository;
import org.example.sba.repository.RoleRepository;
import org.example.sba.service.AccountService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository; // Thêm repository của Role

    @Override
    public long saveAccount(AccountRequestDTO request) {
        // Tìm role USER
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        Account account = Account.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .role(userRole) // Set role USER
                .build();

        accountRepository.save(account);
        log.info("Account has saved successfully with id: {}", account.getId());
        return account.getId();
    }

    @Override
    public UserDetailsService accounDetailsService() {
        return username -> accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
