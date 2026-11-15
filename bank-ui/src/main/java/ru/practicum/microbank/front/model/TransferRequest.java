package ru.practicum.microbank.front.model;

import java.math.BigDecimal;

public record TransferRequest(BigDecimal sum, String toAccountLogin, String fromAccountLogin) {
}
