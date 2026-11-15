package ru.practicum.microbank.accounts.dto;

import java.math.BigDecimal;

public record TransferRequest(BigDecimal sum, String toAccountLogin, String fromAccountLogin) {
}
