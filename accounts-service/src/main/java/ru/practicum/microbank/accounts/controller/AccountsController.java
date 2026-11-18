package ru.practicum.microbank.accounts.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.accounts.client.NotificationsClient;
import ru.practicum.microbank.accounts.dto.*;
import ru.practicum.microbank.accounts.service.AccountService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class AccountsController {

    private final AccountService accountService;
    private final NotificationsClient notificationsClient;

    public AccountsController(AccountService accountService, NotificationsClient notificationsClient) {
        this.accountService = accountService;
        this.notificationsClient = notificationsClient;
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

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public AccountInfo createAccount(@RequestBody AccountInfoShort request) {
        log.trace("Создать счет и пользователя");
        AccountInfo accountInfo = accountService.createAccountAndUser(request);
        notificationsClient.sendNotification(new NotificationRequest(
                "ACCOUNT_CREATED",
                String.format("Создан счет для пользователя %s(%s). Баланс 0 рублей", request.name(), request.login())
        ));
        return accountInfo;
    }

    @PatchMapping("/accounts/{login}")
    @PreAuthorize("hasRole('USER') && hasAuthority('accounts.access')")
    public AccountInfo editAccount(@PathVariable("login") String login, @RequestBody UpdateAccountRequest request) {
        log.trace("Изменить пользователя");
        AccountInfo accountInfo = accountService.updateAccount(login, request);
        notificationsClient.sendNotification(new NotificationRequest(
                "ACCOUNT_INFO_UPDATED",
                String.format("Изменены данные пользователя %s: имя - %s, день рожденья - %s",
                        login, request.name(), request.birthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        ));
        return accountInfo;
    }

    @PostMapping("/accounts/cash/{login}")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('accounts.access')")
    public UpdateBalanceResponse updateBalance(@PathVariable("login") String login, @RequestBody UpdateBalanceRequest request) {
        log.trace("Вызвался метод пополнения/снятия");
        return accountService.updateBalance(login, request);
    }

    @PostMapping("/accounts/transfer")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('accounts.access')")
    public TransferResponse transfer(@RequestBody TransferRequest request) {
        log.trace("Вызвался метод перевода денег");
        return accountService.transfer(request);

    }
}
