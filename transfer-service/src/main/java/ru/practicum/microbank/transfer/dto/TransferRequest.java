package ru.practicum.microbank.transfer.dto;

import java.math.BigDecimal;

public record TransferRequest(BigDecimal sum, String toAccountLogin, String fromAccountLogin) {
}
