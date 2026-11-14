package ru.practicum.microbank.accounts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.accounts.dto.AccountInfo;
import ru.practicum.microbank.accounts.dto.AccountInfoShort;
import ru.practicum.microbank.accounts.dto.UpdateAccountRequest;
import ru.practicum.microbank.accounts.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class AccountsController {

    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public AccountInfo getAccount(@PathVariable("login") String login) {
        log.trace("Найти по  логину {}", login);
        return accountService.findByLogin(login);
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public List<AccountInfoShort> getAccountsInfoShort() {
        log.trace("Вернуть список счетов");

        return accountService.getAccountInfoShorts();
    }

    @PostMapping("/accounts/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public AccountInfo createAccount(@PathVariable("login") String login, @RequestBody AccountInfoShort request) {
        log.trace("Создать счет и пользователя");
        return accountService.createAccountAndUser(login, request);
    }

    @PatchMapping("/accounts/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public AccountInfo editAccount(@PathVariable("login") String login, @RequestBody UpdateAccountRequest request) {
        log.trace("Изменить пользователя");
        return accountService.updateAccount(login, request);
    }
}
