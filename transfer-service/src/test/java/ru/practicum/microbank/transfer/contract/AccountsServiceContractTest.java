package ru.practicum.microbank.transfer.contract;



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
import ru.practicum.microbank.transfer.client.AccountsClient;
import ru.practicum.microbank.transfer.config.TestWebClientConfig;
import ru.practicum.microbank.transfer.dto.TransferRequest;
import ru.practicum.microbank.transfer.dto.TransferResponse;
import ru.practicum.microbank.transfer.exception.DownstreamAccountException;

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
    void transferSuccessStub() {

        TransferResponse response = accountsClient.transfer(
                new TransferRequest(BigDecimal.valueOf(1005,1),
                        "sidorov",
                        "ivanov")
        );

        assertThat(response.fromAccountLogin()).isEqualTo("ivanov");
        assertThat(response.fromAccountBalance()).isEqualByComparingTo("100");
        assertThat(response.toAccountLogin()).isEqualTo("sidorov");
        assertThat(response.message()).isEqualTo("Переведено 100.5 рублей на счет sidorov");
    }

    @Test
    void transferToUnknownAccountStub() {
        try {
            accountsClient.transfer(
                    new TransferRequest(BigDecimal.valueOf(1005,1),
                            "petrov",
                            "ivanov"));
            assertThat(true).as("Ожидается исключение, а его не произошло").isFalse();
        } catch (DownstreamAccountException e) {
            assertThat(e.getMessage()).isEqualTo("Ошибка от accounts-service: 404");
        }
    }

    @Test
    void transferWithLowBalanceStub() {
        try {
            accountsClient.transfer(
                    new TransferRequest(BigDecimal.valueOf(10000),
                            "sidorov",
                            "ivanov"));
            assertThat(true).as("Ожидается исключение, а его не произошло").isFalse();
        } catch (DownstreamAccountException e) {
            assertThat(e.getMessage()).isEqualTo("Ошибка от accounts-service: 409");
        }
    }

}
