package org.example.sba.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.configuration.Translator;
import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.dto.response.AccountDetailResponse;
import org.example.sba.dto.response.ResponseData;
import org.example.sba.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.example.sba.model.Account;
import org.example.sba.model.AccountDocument;
import org.example.sba.modules.account.CreateAccountCommand;
import org.example.sba.modules.account.CreateAccountCommandHandler;
import org.example.sba.modules.account.DeleteAccountCommand;
import org.example.sba.modules.account.DeleteAccountCommandHandler;
import org.example.sba.modules.account.queries.ListAccountQuery;
import org.example.sba.modules.account.queries.ListAccountQueryHandler;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Validated
@Slf4j
@Tag(name = "Account Controller")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final CreateAccountCommandHandler createAccountCommandHandler;
    private final DeleteAccountCommandHandler deleteAccountCommandHandler;
    private final ListAccountQueryHandler listAccountQueryHandler;

    @Operation(summary = "Register new account", description = "Create a new user account with the provided information.")
    @PostMapping(value = "/register")
    public ResponseData<AccountDetailResponse> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        log.info("Request add account, {} {}", request.getFirstName(), request.getLastName());
        AccountDetailResponse accountId = accountService.saveAccount(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("account.add.success"), accountId);
    }

    @Operation(summary = "Register new admin", description = "Create a new admin account. Only users with ROLE_ADMIN can perform this action.")
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

    @Operation(summary = "Create account", description = "Create a new account using CQRS command handler. Returns the created account document from MongoDB.")
    @PostMapping("")
    public AccountDocument createAccount(@RequestBody CreateAccountCommand command) {
        Account account = createAccountCommandHandler.handle(command);
        // Đồng bộ sang MongoDB đã được thực hiện trong handler
        // Trả về bản ghi vừa tạo từ MongoDB (nếu cần)
        return null; // Bạn có thể implement lấy lại từ MongoDB nếu muốn
    }

    @Operation(summary = "Delete account", description = "Delete an account by ID using CQRS command handler.")
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        deleteAccountCommandHandler.handle(new DeleteAccountCommand(id));
    }

    @Operation(summary = "List accounts from MongoDB", description = "Get a paginated list of accounts. Data is read from MongoDB (read model, CQRS query handler). Useful for fast, scalable reads.")
    @GetMapping("")
    public List<AccountDocument> listAccountsFromMongo(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        ListAccountQuery query = new ListAccountQuery();
        query.setPage(page);
        query.setSize(size);
        return listAccountQueryHandler.handle(query);
    }
}