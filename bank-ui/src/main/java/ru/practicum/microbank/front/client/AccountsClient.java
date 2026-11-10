package ru.practicum.microbank.front.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.microbank.front.model.AccountInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class AccountsClient {

    private final WebClient gatewayWebClient;
    private final String gatewayBaseUrl;

    public AccountsClient(WebClient gatewayWebClient,
                          @Value("${bank.gateway.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public AccountInfo getAccountInfo(String login) {
        return gatewayWebClient
                .get()
                .uri(gatewayBaseUrl + "/api/v1/accounts/"+login)
                .retrieve()
                .bodyToMono(AccountInfo.class)
                .block();
    }
}
