package ru.practicum.microbank.accounts.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.microbank.accounts.AbstractIntegrationTest;
import ru.practicum.microbank.accounts.dto.AccountInfoShort;
import ru.practicum.microbank.accounts.dto.CashAction;
import ru.practicum.microbank.accounts.dto.UpdateBalanceRequest;
import ru.practicum.microbank.accounts.dto.UpdateBalanceResponse;
import ru.practicum.microbank.accounts.exception.AccountNotFoundException;
import ru.practicum.microbank.accounts.exception.LowBalanceException;
import ru.practicum.microbank.accounts.repository.AccountRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Должен создавать нового пользователя и счет с нулевым балансом")
    void createAccountAndUser_ShouldCreateNewAccount() {
        var result = accountService.createAccountAndUser(
                new AccountInfoShort("new-user", "Новый Пользователь")
        );

        assertThat(result).isNotNull();
        assertThat(result.login()).isEqualTo("new-user");
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Не должен создавать дубликат, если логин уже занят")
    void createAccountAndUser_WhenLoginExists_ShouldReturnExisting() {
        accountService.createAccountAndUser(new AccountInfoShort("existing", "Уже Существующий"));

        var result = accountService.createAccountAndUser(new AccountInfoShort("existing", "Другое Имя"));

        assertThat(result.login()).isEqualTo("existing");
        assertThat(result.name()).isEqualTo("Уже Существующий"); // имя не изменилось
    }

    @Test
    @DisplayName("Должен успешно снимать деньги при достаточном балансе")
    void updateBalance_WhenWithdrawWithSufficientFunds_ShouldSucceed() {
        accountService.createAccountAndUser(new AccountInfoShort("user1", "User One"));
        accountService.updateBalance("user1", new UpdateBalanceRequest(new BigDecimal("1000.00"), CashAction.PUT));

        UpdateBalanceResponse response = accountService.updateBalance(
                "user1",
                new UpdateBalanceRequest(new BigDecimal("300.00"), CashAction.GET)
        );

        assertThat(response.resultBalance()).isEqualByComparingTo("700.00");
        assertThat(response.message()).contains("снято");
    }

    @Test
    @DisplayName("Должен выбрасывать LowBalanceException при попытке снять больше, чем есть")
    void updateBalance_WhenInsufficientFunds_ShouldThrowException() {
        accountService.createAccountAndUser(new AccountInfoShort("poor-user", "Просто Бедный"));
        accountService.updateBalance("poor-user", new UpdateBalanceRequest(new BigDecimal("100.00"), CashAction.PUT));

        assertThatThrownBy(() ->
                accountService.updateBalance("poor-user", new UpdateBalanceRequest(new BigDecimal("500.00"), CashAction.GET))
        ).isInstanceOf(LowBalanceException.class);
    }

    @Test
    @DisplayName("Должен выбрасывать AccountNotFoundException для несуществующего пользователя")
    void updateBalance_WhenUserNotFound_ShouldThrowException() {
        assertThatThrownBy(() ->
                accountService.updateBalance("ghost", new UpdateBalanceRequest(new BigDecimal("100.00"), CashAction.PUT))
        ).isInstanceOf(AccountNotFoundException.class);
    }


}