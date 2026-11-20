package ru.practicum.microbank.cash.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.cash.client.AccountsClient;
import ru.practicum.microbank.cash.client.NotificationsClient;
import ru.practicum.microbank.cash.dto.CashAction;
import ru.practicum.microbank.cash.dto.NotificationRequest;
import ru.practicum.microbank.cash.dto.UpdateBalanceRequest;
import ru.practicum.microbank.cash.dto.UpdateBalanceResponse;

@RestController
@RequestMapping("/")
@Slf4j
public class CashController {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;

    public CashController(AccountsClient accountsClient, NotificationsClient notificationsClient) {
        this.accountsClient = accountsClient;
        this.notificationsClient = notificationsClient;
    }

    @PostMapping("/cash/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('cash.access')")
    public UpdateBalanceResponse updateBalance(@PathVariable("login") String login, @RequestBody UpdateBalanceRequest request) {
        log.trace("Вызвался метод пополнения/снятия");
        UpdateBalanceResponse response = accountsClient.updateBalance(login, request);
        notificationsClient.sendNotification(createNotificationsRequest(login, request));
        return response;
    }

    private NotificationRequest createNotificationsRequest(String login, UpdateBalanceRequest request) {
        if (CashAction.PUT.equals(request.action())) {
            return new NotificationRequest(
                    "DEPOSIT",
                    String.format("Внесено %s рублей на ЛС пользователя %s", request.sum().toPlainString(), login)
            );
        } else {
            return new NotificationRequest(
                    "WITHDRAW",
                    String.format("Снято %s рублей с ЛС пользователя %s", request.sum().toPlainString(), login)
            );

        }
    }

}
