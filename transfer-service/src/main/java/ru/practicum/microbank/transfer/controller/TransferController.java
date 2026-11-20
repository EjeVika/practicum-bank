package ru.practicum.microbank.transfer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.microbank.transfer.client.AccountsClient;
import ru.practicum.microbank.transfer.client.NotificationsClient;
import ru.practicum.microbank.transfer.dto.NotificationRequest;
import ru.practicum.microbank.transfer.dto.TransferRequest;
import ru.practicum.microbank.transfer.dto.TransferResponse;

@RestController
@RequestMapping("/")
@Slf4j
public class TransferController {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;

    public TransferController(AccountsClient accountsClient, NotificationsClient notificationsClient) {
        this.accountsClient = accountsClient;
        this.notificationsClient = notificationsClient;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') && hasAuthority('transfer.access')")
    public TransferResponse transfer(@RequestBody TransferRequest request) {
        log.trace("Вызвался метод перевода денег");
        TransferResponse response = accountsClient.transfer(request);
        notificationsClient.sendNotification(new NotificationRequest(
                "TRANSFER",
                String.format("Выполнен перевод %s рублей со счета пользователя %s на счет пользователя %s",
                        request.sum().toPlainString(), request.fromAccountLogin(), request.toAccountLogin()))
        );
        return response;
    }
}
