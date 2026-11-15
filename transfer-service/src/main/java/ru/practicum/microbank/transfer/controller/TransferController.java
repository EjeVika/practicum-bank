package ru.practicum.microbank.transfer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.microbank.transfer.client.AccountsClient;
import ru.practicum.microbank.transfer.dto.TransferRequest;
import ru.practicum.microbank.transfer.dto.TransferResponse;

@RestController
@RequestMapping("/")
@Slf4j
public class TransferController {

    private final AccountsClient accountsClient;

    public TransferController(AccountsClient accountsClient) {
        this.accountsClient = accountsClient;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') && hasAuthority('transfer.access')")
    public TransferResponse transfer(@RequestBody TransferRequest request) {
        log.trace("Вызвался метод перевода денег");
        return accountsClient.transfer(request);
    }
}
