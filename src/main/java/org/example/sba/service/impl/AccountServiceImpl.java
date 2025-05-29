package org.example.sba.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.configuration.Translator;
import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.dto.response.AccountDetailResponse;
import org.example.sba.dto.response.PageResponse;
import org.example.sba.exception.ResourceNotFoundException;
import org.example.sba.model.Account;
import org.example.sba.model.AccountHasRole;
import org.example.sba.model.Role;
import org.example.sba.repository.AccountHasRoleRepository;
import org.example.sba.repository.AccountRepository;
import org.example.sba.repository.RoleRepository;
import org.example.sba.service.AccountService;
import org.example.sba.service.EmailService;
import org.example.sba.util.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {

    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountHasRoleRepository accountHasRoleRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AccountDetailResponse saveAccount(AccountRequestDTO request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceNotFoundException(Translator.toLocale("error.username.exist"));
        }
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceNotFoundException(Translator.toLocale("error.email.exist"));
        }

        Account account = Account.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(AccountStatus.INACTIVE)
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .createdBy(request.getUsername())
                .updatedBy(request.getUsername())
                .avatar(request.getAvatar())
                .code(generateCode())
                .build();

        accountRepository.saveAndFlush(account);

        Role accountRole = roleRepository.findByName(USER_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Account role not found"));

        AccountHasRole accountHasRole = new AccountHasRole();
        accountHasRole.setAccount(account);
        accountHasRole.setRole(accountRole);

        accountHasRoleRepository.save(accountHasRole);

        emailService.sendRegistrationConfirmationEmail(account, account.getCode());

        log.info("Account created with id: {} and role: {}, status: INACTIVE, awaiting email confirmation",
                account.getId(), USER_ROLE);

        return convertToAccountDetailResponse(account);
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

    private AccountDetailResponse convertToAccountDetailResponse(Account account) {
        return AccountDetailResponse.builder()
                .id(account.getId())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .dateOfBirth(account.getDateOfBirth())
                .gender(account.getGender())
                .phone(account.getPhone())
                .email(account.getEmail())
                .username(account.getUsername())
                .status(account.getStatus())
                .build();
    }

    @Override
    @Transactional
    public long saveAdmin(AccountRequestDTO request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceNotFoundException(Translator.toLocale("error.username.exist"));
        }
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceNotFoundException(Translator.toLocale("error.email.exist"));
        }

        // Create admin account with ACTIVE status
        Account account = Account.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(AccountStatus.ACTIVE) // Admin defaults to ACTIVE
                .build();

        accountRepository.save(account);

        Role adminRole = roleRepository.findByName(ADMIN_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found"));

        AccountHasRole accountHasRole = new AccountHasRole();
        accountHasRole.setAccount(account);
        accountHasRole.setRole(adminRole);

        accountHasRoleRepository.save(accountHasRole);

        log.info("Admin created with id: {} and role: {}, status: ACTIVE",
                account.getId(), ADMIN_ROLE);
        return account.getId();
    }

    @Override
    public long saveAccount(Account account) {
        return accountRepository.save(account).getId();
    }

    @Override
    public UserDetailsService accountDetailsService() {
        return username -> {
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new RuntimeException("Account is not active. Please confirm your email.");
            }
            return account;
        };
    }

    @Override
    public Account getByUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active. Please confirm your email.");
        }
        return account;
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(Translator.toLocale("error.email.not.found")));
    }

    @Override
    public List<Role> findRolesByAccountId(long accountId) {
        return accountRepository.findRolesByAccountId(accountId);
    }

    @Override
    @Transactional
    public void updateAccount(long accountId, AccountRequestDTO request) {
        Account account = getAccountById(accountId);
        if (!request.getUsername().equals(account.getUsername()) &&
                accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceNotFoundException(Translator.toLocale("error.username.exist"));
        }
        if (!request.getEmail().equals(account.getEmail()) &&
                accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceNotFoundException(Translator.toLocale("error.email.exist"));
        }

        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setDateOfBirth(request.getDateOfBirth());
        account.setGender(request.getGender());
        account.setPhone(request.getPhone());
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        // Status is not updated here; use changeStatus for status updates
        accountRepository.save(account);

        log.info("Account updated successfully, accountId={}", accountId);
    }

    @Override
    @Transactional
    public void changeStatus(long accountId, AccountStatus status) {
        Account account = getAccountById(accountId);
        account.setStatus(status);
        accountRepository.save(account);

        log.info("Account status changed to {}, accountId={}", status, accountId);
    }

    @Override
    @Transactional
    public void deleteAccount(long accountId) {
        Account account = getAccountById(accountId);
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
        log.info("Account set to INACTIVE, accountId={}", accountId);
    }

    @Override
    public AccountDetailResponse getAccount(long accountId) {
        Account account = getAccountById(accountId);
        return AccountDetailResponse.builder()
                .id(accountId)
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .dateOfBirth(account.getDateOfBirth())
                .gender(account.getGender())
                .phone(account.getPhone())
                .email(account.getEmail())
                .username(account.getUsername())
                .status(account.getStatus())
                .build();
    }

    @Override
    public PageResponse<?> getAllAccounts(int pageNo, int pageSize) {
        Page<Account> page = accountRepository.findAll(PageRequest.of(pageNo, pageSize));

        List<AccountDetailResponse> list = page.stream().map(user -> AccountDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }

    private Account getAccountById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Override
    @Transactional
    public void confirmAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account with email " + email + " not found"));
        if (account.getStatus() == AccountStatus.ACTIVE) {
            log.info("Account with email {} is already active", email);
            return;
        }
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        log.info("Account with email updated successfully, email={}", email);
    }
}