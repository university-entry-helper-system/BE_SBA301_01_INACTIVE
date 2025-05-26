package org.example.sba.service;

import org.example.sba.dto.request.AccountRequestDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService {

    long saveAccount(AccountRequestDTO request);

    UserDetailsService accounDetailsService();
}
