package org.example.sba.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.dto.response.AccountDetailResponse;
import org.example.sba.dto.response.PageResponse;
import org.example.sba.model.Account;
import org.example.sba.model.Role;
import org.example.sba.repository.AccountRepository;
import org.example.sba.repository.RoleRepository;
import org.example.sba.service.AccountService;
import org.example.sba.util.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository; // Thêm repository của Role

    @Override
    public long saveAccount(AccountRequestDTO request) {

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
                .build();

        accountRepository.save(account);
        log.info("Account has saved successfully with id: {}", account.getId());
        return account.getId();
    }

    @Override
    public UserDetailsService accountDetailsService() {
        return username -> accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Override
    public Account getByUsername(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Override
    public List<Role> findRolesByAccountId(long accountId) {
        return accountRepository.findRolesByAccountId(accountId);
    }

    @Override
    public void updateAccount(long accountId, AccountRequestDTO request) {
        Account account = getAccountById(accountId);
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setDateOfBirth(request.getDateOfBirth());
        account.setGender(request.getGender());
        account.setPhone(request.getPhone());
        if (!request.getEmail().equals(account.getEmail())) {
            account.setEmail(request.getEmail());
        }
        account.setUsername(request.getUsername());
        account.setPassword(request.getPassword());
        account.setStatus(request.getStatus());
        accountRepository.save(account);

        log.info("Account has updated successfully, accountId={}", accountId);
    }

    @Override
    public void changeStatus(long accountId, AccountStatus status) {
        Account account = getAccountById(accountId);
        account.setStatus(status);
        accountRepository.save(account);

        log.info("Account status has changed successfully, accountId={}", account);
    }

    @Override
    public void deleteAccount(long accountId) {
        accountRepository.deleteById(accountId);
        log.info("User has deleted permanent successfully, accountId={}", accountId);
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
}

