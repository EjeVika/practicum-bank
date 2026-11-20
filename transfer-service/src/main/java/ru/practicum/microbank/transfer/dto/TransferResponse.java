package ru.practicum.microbank.transfer.dto;

import java.math.BigDecimal;

public record TransferResponse(
        BigDecimal sum,
        String toAccountLogin,
        String fromAccountLogin,
        BigDecimal fromAccountBalance,
        String message
) {
}
