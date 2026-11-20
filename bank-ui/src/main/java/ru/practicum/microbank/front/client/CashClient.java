package ru.practicum.microbank.front.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.microbank.front.model.UpdateBalanceRequest;
import ru.practicum.microbank.front.model.UpdateBalanceResponse;

@Component
public class CashClient {

    private final WebClient gatewayWebClient;

    public CashClient(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    public UpdateBalanceResponse updateBalance(String login, UpdateBalanceRequest request) {
        return gatewayWebClient
                .post()
                .uri("/api/v1/cash/{login}", login)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.CONFLICT,
                        response -> Mono.error(new RuntimeException("Недостаточно средств для снятия со счета."))
                )
                .bodyToMono(UpdateBalanceResponse.class)
                .block();
    }
}
