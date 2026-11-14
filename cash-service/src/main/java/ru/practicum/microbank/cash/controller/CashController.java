package ru.practicum.microbank.cash.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.cash.dto.UpdateBalanceRequest;

@RestController
@RequestMapping("/")
@Slf4j
public class CashController {

    @PostMapping("/cash/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('cash.access')")
    public String editCash(@PathVariable("login") String login, @RequestBody UpdateBalanceRequest request
    ) {
        return "OK";
    }
}
