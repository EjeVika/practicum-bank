package ru.practicum.microbank.cash.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.cash.client.AccountsClient;
import ru.practicum.microbank.cash.dto.UpdateBalanceRequest;
import ru.practicum.microbank.cash.dto.UpdateBalanceResponse;

@RestController
@RequestMapping("/")
@Slf4j
public class CashController {

    private final AccountsClient accountsClient;

    public CashController(AccountsClient accountsClient) {
        this.accountsClient = accountsClient;
    }

    @PostMapping("/cash/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('cash.access')")
    public UpdateBalanceResponse updateBalance(@PathVariable("login") String login, @RequestBody UpdateBalanceRequest request) {
        log.trace("Вызвался метод пополнения/снятия");
        return accountsClient.updateBalance(login, request);
    }
}
