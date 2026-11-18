package ru.practicum.microbank.cash.contract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.microbank.cash.client.AccountsClient;
import ru.practicum.microbank.cash.config.TestWebClientConfig;
import ru.practicum.microbank.cash.dto.CashAction;
import ru.practicum.microbank.cash.dto.UpdateBalanceRequest;
import ru.practicum.microbank.cash.dto.UpdateBalanceResponse;
import ru.practicum.microbank.cash.exception.DownstreamAccountException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "ru.practicum.microbank:accounts-service:+:stubs:8882", // группа:артефакт:версия:классификатор:порт
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("contract-test")
@Import(TestWebClientConfig.class)
public class AccountsServiceContractTest {

    @Autowired
    @Qualifier("testGatewayWebClient")
    private WebClient gatewayClient;
    private AccountsClient accountsClient;

    @BeforeEach
    void setUp() {
        accountsClient = new AccountsClient(gatewayClient);
    }

    @Test
    void depositToAccountSuccessStub() {

        UpdateBalanceResponse response = accountsClient.updateBalance("ivanov",
                new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.PUT));

        assertThat(response.login()).isEqualTo("ivanov");
        assertThat(response.resultBalance()).isEqualByComparingTo("200.5");
        assertThat(response.sum()).isEqualByComparingTo("100.5");
        assertThat(response.action()).isEqualTo(CashAction.PUT);
        assertThat(response.message()).isEqualTo("На счет успешно положено 100.5 рублей");
    }

    @Test
    void depositToAccountFailsStub() {
        try {
            accountsClient.updateBalance("petrov",
                    new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.PUT));
            assertThat(false);
        } catch (DownstreamAccountException e) {

            assertThat(assertThat(e.getMessage()).contains("Ошибка от accounts-service: 404"));
        }
    }

    @Test
    void withdrawFromAccountSuccessStub() {

        UpdateBalanceResponse response = accountsClient.updateBalance("sidorov",
                new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.GET));

        assertThat(response.login()).isEqualTo("sidorov");
        assertThat(response.resultBalance()).isEqualByComparingTo("200.");
        assertThat(response.sum()).isEqualByComparingTo("100.5");
        assertThat(response.action()).isEqualTo(CashAction.GET);
        assertThat(response.message()).isEqualTo("На счет успешно положено 100.5 рублей");
    }

    @Test
    void withdrawFromAccountFailsStub() {

        try {
            accountsClient.updateBalance("fedorov",
                    new UpdateBalanceRequest(BigDecimal.valueOf(1005,1), CashAction.GET));
            assertThat(false);
        } catch (DownstreamAccountException e) {

            assertThat(assertThat(e.getMessage()).contains("Ошибка от accounts-service: 409"));
        }

    }


}
