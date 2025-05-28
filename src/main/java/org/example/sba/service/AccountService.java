package org.example.sba.service;

import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.dto.response.AccountDetailResponse;
import org.example.sba.dto.response.PageResponse;
import org.example.sba.model.Account;
import org.example.sba.model.Role;
import org.example.sba.util.AccountStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService {

    long saveAccount(AccountRequestDTO request);

    long saveAdmin(AccountRequestDTO request);

    long saveAccount(Account account);

    UserDetailsService accountDetailsService();

    Account getByUsername(String username);

    Account getAccountByEmail(String email);

    List<Role> findRolesByAccountId(long accountId);

    void updateAccount(long accountId, AccountRequestDTO request);

    void changeStatus(long accountId, AccountStatus status);

    void deleteAccount(long accountId);

    AccountDetailResponse getAccount(long accountId);

    PageResponse<?> getAllAccounts(int pageNo, int pageSize);

    void confirmAccountByEmail(String email);
}
