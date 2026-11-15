package ru.practicum.microbank.accounts.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.microbank.accounts.dto.NotificationRequest;
import ru.practicum.microbank.accounts.dto.NotificationResponse;


@Component
public class NotificationsClient {

    private final WebClient gatewayWebClient;

    public NotificationsClient(WebClient gatewayWebClient) {
        this.gatewayWebClient = gatewayWebClient;
    }

    public NotificationResponse sendNotification(NotificationRequest request) {
        return gatewayWebClient
                .post()
                .uri("/notify")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NotificationResponse.class)
                .block();
    }

}
