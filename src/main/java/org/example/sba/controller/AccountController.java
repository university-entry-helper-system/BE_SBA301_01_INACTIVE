package org.example.sba.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.configuration.Translator;
import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.dto.response.AccountDetailResponse;
import org.example.sba.dto.response.PageResponse;
import org.example.sba.dto.response.ResponseData;
import org.example.sba.dto.response.ResponseError;
import org.example.sba.service.AccountService;
import org.example.sba.util.AccountStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Validated
@Slf4j
@Tag(name = "Account Controller")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(method = "POST", summary = "Add new account", description = "Create new account")
    @PostMapping(value = "/register")
    public ResponseData<AccountDetailResponse> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        log.info("Request add account, {} {}", request.getFirstName(), request.getLastName());
        AccountDetailResponse accountId = accountService.saveAccount(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("account.add.success"), accountId);
    }

    @Operation(method = "POST", summary = "Add new admin", description = "Create new admin")
    @PostMapping(value = "/admin")
    public ResponseData<AccountDetailResponse> createAdmin(@Valid @RequestBody AccountRequestDTO request) {
        log.info("Request add admin, {} {}", request.getFirstName(), request.getLastName());
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You do not have permission to create an admin.");
        }
        AccountDetailResponse accountId = accountService.saveAdmin(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("admin.add.success"), accountId);
    }

    @Operation(summary = "Update account", description = "Send a request via this API to update account")
    @PutMapping("/{accountId}")
    public ResponseData<?> updateAccount(@PathVariable @Min(1) long accountId, @Valid @RequestBody AccountRequestDTO request) {
        log.info("Request update accountId={}", accountId);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isUser = authorities.contains(new SimpleGrantedAuthority("ROLE_USER"));

        AccountDetailResponse account = accountService.getAccount(accountId);

        // If user has ROLE_USER, they can only update their own account
        // If user has ROLE_ADMIN, they can update any account
        if (!isAdmin && (!isUser || !currentUsername.equals(account.getUsername()))) {
            throw new AccessDeniedException("You do not have permission to update this account.");
        }

        try {
            accountService.updateAccount(accountId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("account.upd.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update account fail");
        }
    }


    @Operation(summary = "Change status of account", description = "Send a request via this API to change status of account")
    @PatchMapping("/{accountId}")
    public ResponseData<?> updateStatus(@Min(1) @PathVariable long accountId, @RequestParam AccountStatus status) {
        log.info("Request change status, accountId={}", accountId);
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You do not have permission to change account status.");
        }
        try {
            accountService.changeStatus(accountId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("account.change.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update account status fail");
        }
    }

    @Operation(summary = "Delete account permanently", description = "Send a request via this API to delete account permanently")
    @DeleteMapping("/{accountId}")
    public ResponseData<?> deleteAccount(@PathVariable @Min(value = 1, message = "accountId must be greater than 0") long accountId) {
        log.info("Request delete accountId={}", accountId);
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You do not have permission to delete an account.");
        }
        try {
            accountService.deleteAccount(accountId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("account.del.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete account fail");
        }
    }

    @Operation(summary = "Get account detail", description = "Send a request via this API to get account information")
    @GetMapping("/{accountId}")
    public ResponseData<?> getAccount(@PathVariable @Min(1) long accountId) {
        log.info("Request get account detail, accountId={}", accountId);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isUser = authorities.contains(new SimpleGrantedAuthority("ROLE_USER"));

        AccountDetailResponse account = accountService.getAccount(accountId);

        // If user has ROLE_USER, they can only access their own account
        // If user has ROLE_ADMIN, they can access any account
        if (!isAdmin && (!isUser || !currentUsername.equals(account.getUsername()))) {
            throw new AccessDeniedException("You do not have permission to view this account.");
        }

        try {
            return new ResponseData<>(HttpStatus.OK.value(), "account", account);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of accounts per pageNo", description = "Send a request via this API to get account list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<?> getAllAccounts(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                          @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You do not have permission to view all accounts.");
        }
        try {
            PageResponse<?> accounts = accountService.getAllAccounts(pageNo, pageSize);
            return new ResponseData<>(HttpStatus.OK.value(), "accounts", accounts);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Confirm email", description = "Confirm email")
    @GetMapping("/confirm")
    public ResponseData<String> confirmRegistration(@RequestParam String email) {
        try {
            accountService.confirmAccountByEmail(email);
            return new ResponseData<>(HttpStatus.OK.value(),
                    Translator.toLocale("account.confirm.success"),
                    "Account successfully confirmed. You can now log in.");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Email confirmation failed");
        }
    }
}