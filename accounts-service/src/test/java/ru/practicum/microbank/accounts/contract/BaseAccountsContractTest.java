package ru.practicum.microbank.accounts.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.microbank.accounts.client.NotificationsClient;
import ru.practicum.microbank.accounts.dto.*;
import ru.practicum.microbank.accounts.exception.AccountNotFoundException;
import ru.practicum.microbank.accounts.exception.LowBalanceException;
import ru.practicum.microbank.accounts.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("contract-test")
public abstract class BaseAccountsContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AccountService accountService;
    @MockitoBean
    protected NotificationsClient notificationsClient;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        // mocks for bank-ui
        when(accountService.findByLogin("ivanov"))
                .thenReturn(new AccountInfo(
                        "ivanov",
                        "Иван Иванов",
                        LocalDate.of(1990, 1, 15), BigDecimal.valueOf(150050, 2))
                );

        when(accountService.findByLogin("petrov")).thenThrow(new AccountNotFoundException("petrov"));

        when(accountService.getAccountInfoShorts()).thenReturn(List.of(
                new AccountInfoShort("ivanov", "Иван Иванов"),
                new AccountInfoShort("petrov", "Петр Петров")));

        when(accountService.createAccountAndUser("ivanov",
                new AccountInfoShort("ivanov", "Иван Иванов")))
                .thenReturn(new AccountInfo("ivanov", "Иван Иванов", null, BigDecimal.ZERO));

        when(accountService.updateAccount("ivanov",
                new UpdateAccountRequest("Иван Иванов", LocalDate.of(1990, 1, 15))))
                .thenReturn(new AccountInfo("ivanov", "Иван Иванов",
                        LocalDate.of(1990, 1, 15), BigDecimal.valueOf(100)));

        when(accountService.updateAccount("petrov",
                new UpdateAccountRequest("Иван Иванов", LocalDate.of(1990, 1, 15))))
                .thenThrow(new AccountNotFoundException("petrov"));
        // mocks for cash-service
        when(accountService.updateBalance("ivanov",
                new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.PUT)))
                .thenReturn(new UpdateBalanceResponse(
                        "ivanov",
                        BigDecimal.valueOf(2005,1),
                        BigDecimal.valueOf(1005,1),
                        CashAction.PUT,
                        "На счет успешно положено 100.5 рублей"
                        ));

        when(accountService.updateBalance("petrov",
                new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.PUT)))
                .thenThrow(new AccountNotFoundException("petrov"));

        when(accountService.updateBalance("sidorov",
                new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.GET)))
                .thenReturn(new UpdateBalanceResponse(
                        "sidorov",
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(1005,1),
                        CashAction.GET,
                        "На счет успешно положено 100.5 рублей"
                ));

        when(accountService.updateBalance("fedorov",
                new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.GET)))
                .thenThrow(new LowBalanceException("fedorov"));


        when(notificationsClient.sendNotification(mock(NotificationRequest.class)))
                .thenReturn(mock(NotificationResponse.class));

    }
}
