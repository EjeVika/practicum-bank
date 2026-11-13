package ru.practicum.microbank.front.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.front.client.AccountsClient;
import ru.practicum.microbank.front.controller.stub.AccountStub;
import ru.practicum.microbank.front.exception.AccountNotFoundException;
import ru.practicum.microbank.front.model.*;

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

    public MainController(AccountsClient accountsClient) {
        this.accountsClient = accountsClient;
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
        try {
            AccountInfo accountInfo = accountsClient.getAccountInfo(oidcUser.getPreferredUsername());
            model.addAttribute("name", accountInfo.name());
            model.addAttribute("birthdate", accountInfo.birthDay().format(DateTimeFormatter.ISO_DATE));
            model.addAttribute("sum", accountInfo.balance());
        } catch (AccountNotFoundException e) {
            log.info("Пользователь не найден, создаем нового");
            String login = oidcUser.getPreferredUsername();
            String name = oidcUser.getFullName();
            accountsClient.createAccount(
                    new CreateAccountRequest(login, name));
            model.addAttribute("name", name);
            model.addAttribute("sum", String.valueOf(0));
            model.addAttribute("info", "Создан новый счет. Заполните персональные данные");
        }
        List<AccountInfoShort> accountsInfoShort = accountsClient.getAccountsInfoShort();
        accountsInfoShort = accountsInfoShort
                .stream()
                .filter(accountInfoShort -> !accountInfoShort.login().equals(oidcUser.getPreferredUsername()))
                .toList();
        model.addAttribute("accounts", accountsInfoShort);

        return "main";
    }

    /**
     * PATCH /accounts.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для изменения данных текущего пользователя по REST
     * <p>
     * Изменяемые данные:
     * 1. name - Фамилия Имя
     * 2. birthdate - дата рождения в формате YYYY-DD-MM
     */
    @PatchMapping("/accounts")
    public String editAccount(
            Model model,
            @RequestParam("name") String name,
            @RequestParam("birthdate") LocalDate birthdate,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (oidcUser != null) {
            model.addAttribute("errors", "Ошибка авторизации пользователя при попытке редактирования");
            return "main";
        }
        UpdateAccountRequest request = new UpdateAccountRequest(name, birthdate);
        AccountInfo accountInfo = accountsClient.editAccount(oidcUser.getPreferredUsername(), request);
        // TODO: Заменить на то, что описано в комментарии к методу
        accountStub.setNameAndBirthdate(name, birthdate);
        //accountStub.fillMNodel(model, null, null);

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
    @PostMapping("/cash")
    public String editCash(
            Model model,
            @RequestParam("value") int value,
            @RequestParam("action") CashAction action
    ) {
        // TODO: Заменить на то, что описано в комментарии к методу
        accountStub.editCash(model, value, action);

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
    @PostMapping("/transfer")
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

        return "main";
    }
}
