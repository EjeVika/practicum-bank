package ru.practicum.microbank.transfer.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.microbank.transfer.dto.NotificationRequest;
import ru.practicum.microbank.transfer.dto.NotificationResponse;


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
