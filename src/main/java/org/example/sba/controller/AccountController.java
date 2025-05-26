package org.example.sba.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.sba.configuration.Translator;
import org.example.sba.dto.request.AccountRequestDTO;
import org.example.sba.dto.response.ResponseData;
import org.example.sba.service.impl.AccountServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
@Validated
@Slf4j
@Tag(name = "Account Controller")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

    @Operation(method = "POST", summary = "Add new account", description = "Create new account")
    @PostMapping(value = "/")
    public ResponseData<Long> Account(@Valid @RequestBody AccountRequestDTO request) {
        log.info("Request add account, {} {}", request.getFirstName(), request.getLastName());
        long accountId = accountService.saveAccount(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("account.add.success"), accountId);
    }

}
