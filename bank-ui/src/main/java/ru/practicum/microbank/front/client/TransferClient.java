package ru.practicum.microbank.front.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.microbank.front.model.TransferRequest;
import ru.practicum.microbank.front.model.TransferResponse;

@Component
public class TransferClient {

    private final WebClient gatewayWebClient;

    public TransferClient(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    public TransferResponse transfer(TransferRequest request) {
        return gatewayWebClient
                .post()
                .uri("/api/v1/transfer")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.CONFLICT,
                        response ->
                                Mono.error(new RuntimeException("Недостаточно средств для снятия со счета."))
                )
                .bodyToMono(TransferResponse.class)
                .block();
    }
}
