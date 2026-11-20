package ru.practicum.microbank.transfer.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
@Profile("contract-test")
public class TestWebClientConfig {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public TestWebClientConfig(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public WebClient.Builder testGatewayWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("http://localhost:8882")
                .filter(addAccessTokenHeader());
    }

    @Bean
    @Primary
    public WebClient testGatewayWebClient() {
        return testGatewayWebClientBuilder()
                .build();
    }

    /**
     * Фильтр для передачи тестового JWT-токена в Gateway API
     */
    private ExchangeFilterFunction addAccessTokenHeader() {
        return (request, next) -> {
            ClientRequest requestWithToken = ClientRequest.from(request)
                    .header("Authorization", "Bearer " + "test-token")
                    .build();
            return next.exchange(requestWithToken);
        };
    }
}

