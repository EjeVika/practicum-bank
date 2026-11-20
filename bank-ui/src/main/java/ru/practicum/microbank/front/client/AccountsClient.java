package ru.practicum.microbank.front.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.microbank.front.exception.AccountNotFoundException;
import ru.practicum.microbank.front.model.AccountInfo;
import ru.practicum.microbank.front.model.AccountInfoShort;
import ru.practicum.microbank.front.model.UpdateAccountRequest;

import java.util.List;

@Component
public class AccountsClient {

    private final WebClient gatewayWebClient;

    public AccountsClient(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    public AccountInfo getAccountInfo(String login) {
        return gatewayWebClient
                .get()
                .uri("/api/v1/accounts/{login}", login)
                .retrieve()
                // 404 → AccountNotFoundException
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new AccountNotFoundException(login))
                )
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(AccountInfo.class)
                .block();
    }

    public List<AccountInfoShort> getAccountsInfoShort() {
        return gatewayWebClient
                .get()
                .uri("/api/v1/accounts")
                .retrieve()
                .bodyToFlux(AccountInfoShort.class)
                .collectList()
                .block();
    }

    public AccountInfo editAccount(String login, UpdateAccountRequest request) {
        return gatewayWebClient
                .patch()
                .uri("/api/v1/accounts/{login}", login)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountInfo.class)
                .block();
    }

    public AccountInfo createAccount(AccountInfoShort accountInfoShort) {
        return gatewayWebClient
                .post()
                .uri("/api/v1/accounts")
                .bodyValue(accountInfoShort)
                .retrieve()
                .bodyToMono(AccountInfo.class)
                .block();
    }
}
