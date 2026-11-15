package ru.practicum.microbank.front.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.microbank.front.client.AccountsClient;
import ru.practicum.microbank.front.client.CashClient;
import ru.practicum.microbank.front.controller.stub.AccountStub;
import ru.practicum.microbank.front.exception.AccountNotFoundException;
import ru.practicum.microbank.front.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Контроллер main.html.
 * <p>
 * Используемая модель для main.html:
 * model.addAttribute("name", name);
 * model.addAttribute("birthdate", birthdate.format(DateTimeFormatter.ISO_DATE));
 * model.addAttribute("sum", sum);
 * model.addAttribute("accounts", accounts);
 * model.addAttribute("errors", errors);
 * model.addAttribute("info", info);
 * <p>
 * Поля модели:
 * name - Фамилия Имя текущего пользователя, String (обязательное)
 * birthdate - дата рождения текущего пользователя, String в формате 'YYYY-MM-DD' (обязательное)
 * sum - сумма на счету текущего пользователя, Integer (обязательное)
 * accounts - список аккаунтов, которым можно перевести деньги, List<AccountDto> (обязательное)
 * errors - список ошибок после выполнения действий, List<String> (не обязательное)
 * info - строка успешности после выполнения действия, String (не обязательное)
 * <p>
 * С примерами использования можно ознакомиться в тестовом классе заглушке AccountStub
 */
@Controller
@Slf4j
public class MainController {

    private final AccountsClient accountsClient;
    private final CashClient cashClient;

    public MainController(AccountsClient accountsClient, CashClient cashClient) {
        this.accountsClient = accountsClient;
        this.cashClient = cashClient;
    }


    // TODO: Удалить заглушку, так как используется только для ознакомительных целей
    @Autowired
    private AccountStub accountStub;

    /**
     * GET /.
     * Редирект на GET /account
     */
    @GetMapping
    public String index() {
        return "redirect:/api/v1/accounts";
    }

    /**
     * GET /accounts.
     * 1) Выполняем запрос в сервис accounts-service для получения инфо счета по логину
     * Если такого не найдено то создаем его с имеющейся инфой
     * 2) Выполняем запрос в сервис accounts-service для получения краткой информации о всех счетах
     */
    @GetMapping("/api/v1/accounts")
    public String getAccount(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            model.addAttribute("errors", "Ошибка авторизации пользователя");
            return "main";
        }
        populateModel(model, oidcUser);
        return "main";
    }

    /**
     * Загружает полные данные текущего пользователя и список счетов
     */
    private void populateModel(Model model, OidcUser oidcUser) {
        String login = oidcUser.getPreferredUsername();
        try {
            AccountInfo accountInfo = accountsClient.getAccountInfo(login);
            model.addAttribute("name", accountInfo.name());
            model.addAttribute("birthdate",
                    accountInfo.birthday() == null ? null : accountInfo.birthday().format(DateTimeFormatter.ISO_DATE));
            model.addAttribute("sum", accountInfo.balance());
        } catch (AccountNotFoundException e) {
            log.info("Пользователь не найден, создаем нового");
            String name = oidcUser.getFullName();
            accountsClient.createAccount(new AccountInfoShort(login, name));
            model.addAttribute("name", name);
            model.addAttribute("sum", String.valueOf(0));
            model.addAttribute("info", "Создан новый счет. Заполните персональные данные");
        } catch (Exception e) {
            log.warn("Не удалось загрузить данные аккаунта при восстановлении модели", e);
            model.addAttribute("name", oidcUser.getFullName());
            model.addAttribute("sum", "0");
        }
        populateAccountsList(model, login);
    }

    private void populateAccountsList(Model model, String currentLogin) {
        try {
            List<AccountInfoShort> accountsInfoShort = accountsClient.getAccountsInfoShort()
                    .stream()
                    .filter(accountInfoShort -> !accountInfoShort.login().equals(currentLogin))
                    .toList();
            model.addAttribute("accounts", accountsInfoShort);
        } catch (Exception e) {
            log.warn("Не удалось загрузить список счетов для переводов", e);
            model.addAttribute("accounts", List.of());
        }
    }

    /**
     * POST /accounts.
     * Что нужно сделать:
     * 1. Идет в сервис accounts через Gateway API для изменения данных текущего пользователя по REST
     * <p>
     * Изменяемые данные:
     * 1. name - Фамилия Имя
     * 2. birthdate - дата рождения в формате YYYY-DD-MM
     */
    @PostMapping("/api/v1/accounts")
    public String editAccount(
            Model model,
            @RequestParam("name") String name,
            @RequestParam("birthdate") LocalDate birthdate,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (oidcUser == null) {
            model.addAttribute("errors", "Ошибка авторизации пользователя при попытке редактирования");
            return "main";
        }
        UpdateAccountRequest request = new UpdateAccountRequest(name, birthdate);
        accountsClient.editAccount(oidcUser.getPreferredUsername(), request);

        populateModel(model, oidcUser);
        model.addAttribute("info", "Данные сохранены");
        return "main";
    }

    /**
     * POST /cash.
     * Что нужно сделать:
     * 1. Сходить в сервис cash через Gateway API для снятия/пополнения счета текущего аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Параметры:
     * 1. value - сумма списания
     * 2. action - GET (снять), PUT (пополнить)
     */
    @PostMapping("/api/v1/cash")
    public String editCash(
            Model model,
            @RequestParam("value") double sum,
            @RequestParam("action") CashAction action,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (oidcUser == null) {
            model.addAttribute("errors", "Ошибка авторизации пользователя при пополнении/снятии");
            return "main";
        }
        UpdateBalanceRequest request = new UpdateBalanceRequest(BigDecimal.valueOf(sum), action);
        try {
            UpdateBalanceResponse response = cashClient.updateBalance(oidcUser.getPreferredUsername(), request);
            model.addAttribute("info", response.message());
        } catch (Exception e) {
            model.addAttribute("errors", "Операция пополнения/снятия не выполнена: " + e.getLocalizedMessage());
            model.addAttribute("value", sum);
        }
        populateModel(model, oidcUser);
        return "main";
    }

    /**
     * POST /transfer.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для перевода со счета текущего аккаунта на счет другого аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Параметры:
     * 1. value - сумма списания
     * 2. login - логин пользователя получателя
     */
    @PostMapping("/api/v1/transfer")
    public String transfer(
            Model model,
            @RequestParam("value") int value,
            @RequestParam("login") String login
    ) {
        // TODO: Заменить на то, что описано в комментарии к методу
        accountStub.transfer(model, value, login);

        return "main";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleWebClientResponseException(
            RuntimeException exception,
            Model model,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        log.error("Ошибка под пользователем {}", oidcUser != null ? oidcUser.getPreferredUsername() : null, exception);
        model.addAttribute("errors", "Внутренняя ошибка. Обратитесь к администратору. " + exception.getMessage());

        if (oidcUser != null) {
            populateModel(model, oidcUser);
        }
        return "main";
    }
}