package ru.practicum.microbank.accounts.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.microbank.accounts.dto.AccountInfoShort;
import ru.practicum.microbank.accounts.dto.CashAction;
import ru.practicum.microbank.accounts.dto.UpdateBalanceRequest;
import ru.practicum.microbank.accounts.dto.UpdateBalanceResponse;
import ru.practicum.microbank.accounts.exception.AccountNotFoundException;
import ru.practicum.microbank.accounts.exception.LowBalanceException;
import ru.practicum.microbank.accounts.mapper.AccountMapper;
import ru.practicum.microbank.accounts.mapper.UserMapper;
import ru.practicum.microbank.accounts.model.Account;
import ru.practicum.microbank.accounts.model.User;
import ru.practicum.microbank.accounts.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("test-user");
        testUser.setFirstName("Test");
        testUser.setLastName("Test");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUser(testUser);
        testAccount.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void updateBalanceGetSuccessTest() {
        UpdateBalanceRequest request = new UpdateBalanceRequest(new BigDecimal("90.00"), CashAction.GET);
        when(accountRepository.findByLogin("test-user")).thenReturn(Optional.of(testAccount));

        UpdateBalanceResponse response = accountService.updateBalance("test-user", request);

        assertThat(response.resultBalance()).isEqualByComparingTo("10.00");
        assertThat(response.message()).contains("успешно снято");
    }

    @Test
    void updateBalanceGetFailTest() {
        UpdateBalanceRequest request = new UpdateBalanceRequest(new BigDecimal("500.00"), CashAction.GET);
        when(accountRepository.findByLogin("test-user")).thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.updateBalance("test-user", request))
                .isInstanceOf(LowBalanceException.class)
                .hasMessageContaining("Недостаточно средств");

        verify(accountRepository, never()).saveAndFlush(any(Account.class));
    }

    @Test
    void updateBalancePutSuccessTest() {
        UpdateBalanceRequest request = new UpdateBalanceRequest(new BigDecimal("500.00"), CashAction.PUT);
        when(accountRepository.findByLogin("test-user")).thenReturn(Optional.of(testAccount));

        UpdateBalanceResponse response = accountService.updateBalance("test-user", request);

        assertThat(response.resultBalance()).isEqualByComparingTo("600.00");
        assertThat(response.message()).contains("успешно положено");
    }

    @Test
    void findByLoginFailTest() {
        when(accountRepository.findByLogin("unknown-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findByLogin("unknown-user"))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
