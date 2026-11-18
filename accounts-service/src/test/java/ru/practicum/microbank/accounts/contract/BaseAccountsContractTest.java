package ru.practicum.microbank.accounts.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.microbank.accounts.dto.AccountInfo;
import ru.practicum.microbank.accounts.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("contract-test")
public abstract class BaseAccountsContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AccountService accountService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        when(accountService.findByLogin("ivanov"))
                .thenReturn(new AccountInfo(
                        "ivanov",
                        "Иван Иванов",
                        LocalDate.of(1990, 1, 15), BigDecimal.valueOf(150050, 2)));

    }
}
