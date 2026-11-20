package ru.practicum.microbank.cash.exception;

import org.springframework.http.HttpStatusCode;

public class DownstreamAccountException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String responseBody;

    public DownstreamAccountException(HttpStatusCode statusCode, String responseBody) {
        super("Ошибка от accounts-service: " + statusCode.value());
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
