package ru.practicum.microbank.transfer.dto;

import java.math.BigDecimal;

public record TransferRequest(String fromLogin, String toLogin, BigDecimal sum) {
}
