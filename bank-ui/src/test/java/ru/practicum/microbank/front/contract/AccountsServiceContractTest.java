package ru.practicum.microbank.front.contract;

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
import ru.practicum.microbank.front.client.AccountsClient;
import ru.practicum.microbank.front.config.TestWebClientConfig;
import ru.practicum.microbank.front.exception.AccountNotFoundException;
import ru.practicum.microbank.front.model.AccountInfo;
import ru.practicum.microbank.front.model.AccountInfoShort;
import ru.practicum.microbank.front.model.UpdateAccountRequest;

import java.time.LocalDate;
import java.util.List;

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
    void shouldGetAccountFromStub() {

        AccountInfo accountInfo = accountsClient.getAccountInfo("ivanov");

        assertThat(accountInfo.login()).isEqualTo("ivanov");
        assertThat(accountInfo.name()).isEqualTo("Иван Иванов");
        assertThat(accountInfo.birthday()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(accountInfo.balance()).isEqualByComparingTo("1500.5");
    }

    @Test
    void failToGetAccountFromStub() {
        try {
            accountsClient.getAccountInfo("petrov");
            assertThat(true).as("Ожидается исключение, а его не произошло").isFalse();
        } catch (AccountNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Не найден счет для пользователя: petrov");
        }
    }

    @Test
    void getAccountsInfoShortFromStub() {
        List<AccountInfoShort> accounts = accountsClient.getAccountsInfoShort();
        assertThat(accounts.size()).isEqualTo(2);
        assertThat(accounts.get(0).login()).isEqualTo("ivanov");
        assertThat(accounts.get(0).name()).isEqualTo("Иван Иванов");
        assertThat(accounts.get(1).login()).isEqualTo("petrov");
        assertThat(accounts.get(1).name()).isEqualTo("Петр Петров");
    }

    @Test
    void createAccountFromStub() {
        AccountInfo accountInfo = accountsClient.createAccount(new AccountInfoShort("ivanov", "Иван Иванов"));
        assertThat(accountInfo.login()).isEqualTo("ivanov");
        assertThat(accountInfo.name()).isEqualTo("Иван Иванов");
        assertThat(accountInfo.birthday()).isNull();
        assertThat(accountInfo.balance()).isEqualByComparingTo("0");
    }

    @Test
    void editAccountFromStub() {
        AccountInfo accountInfo = accountsClient.editAccount("ivanov",
                new UpdateAccountRequest( "Иван Иванов", LocalDate.of(1990,1,15)));
        assertThat(accountInfo.login()).isEqualTo("ivanov");
        assertThat(accountInfo.name()).isEqualTo("Иван Иванов");
        assertThat(accountInfo.birthday()).isEqualTo(LocalDate.of(1990,1,15));
        assertThat(accountInfo.balance()).isEqualByComparingTo("100");
    }

    @Test
    void failToAccountFromStub() {
        try {
            accountsClient.editAccount("petrov",
                    new UpdateAccountRequest( "Иван Иванов", LocalDate.of(1990,1,15)));
            assertThat(true).as("Ожидается исключение, а его не произошло").isFalse();
        } catch (AccountNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("Не найден счет для пользователя: petrov");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("404 Not Found");
        }

    }


}
