package ru.practicum.microbank.accounts.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.accounts.model.AccountInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/")
public class AccountsController {

    @GetMapping("/accounts/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public AccountInfo getAccounts() {
        return new AccountInfo("login", "name", LocalDate.of(2020,01,01), BigDecimal.ZERO);
    }
}
