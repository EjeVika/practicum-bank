package ru.practicum.microbank.cash.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.microbank.cash.dto.UpdateBalanceRequest;
import ru.practicum.microbank.cash.dto.UpdateBalanceResponse;
import ru.practicum.microbank.cash.exception.DownstreamAccountException;

@Component
public class AccountsClient {

    private final WebClient gatewayWebClient;

    public AccountsClient(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    public UpdateBalanceResponse updateBalance(String login, UpdateBalanceRequest request) {
        return gatewayWebClient
                .post()
                .uri("/accounts/cash/{login}", login)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new DownstreamAccountException(response.statusCode(), errorBody)
                                ))
                )
                .bodyToMono(UpdateBalanceResponse.class)
                .block();
    }

}
