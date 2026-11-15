package ru.practicum.microbank.transfer.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.microbank.transfer.dto.TransferRequest;
import ru.practicum.microbank.transfer.dto.TransferResponse;
import ru.practicum.microbank.transfer.exception.DownstreamAccountException;

@Component
public class AccountsClient {

    private final WebClient gatewayWebClient;

    public AccountsClient(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    public TransferResponse transfer(TransferRequest request) {
        return gatewayWebClient
                .post()
                .uri("/accounts/transfer")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new DownstreamAccountException(response.statusCode(), errorBody)
                                ))
                )
                .bodyToMono(TransferResponse.class)
                .block();
    }

}
