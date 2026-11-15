package ru.practicum.microbank.transfer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TransferGlobalExceptionHandler {

    @ExceptionHandler(DownstreamAccountException.class)
    public ResponseEntity<String> handleDownstreamAccountError(DownstreamAccountException e) {
        log.warn("Ошибка accounts-service: statusCode={}, body={}", e.getStatusCode(), e.getResponseBody());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(e.getResponseBody());
    }
}
