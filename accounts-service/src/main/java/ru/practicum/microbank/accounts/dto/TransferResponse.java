package ru.practicum.microbank.accounts.dto;

import java.math.BigDecimal;

public record TransferResponse(
        BigDecimal sum,
        String toAccountLogin,
        BigDecimal toAccountBalance,
        String fromAccountLogin,
        BigDecimal fromAccountBalance,
        String message
) {
}
