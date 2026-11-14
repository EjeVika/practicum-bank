package ru.practicum.microbank.accounts.dto;

import java.math.BigDecimal;

public record TransferRequest(String fromLogin, String toLogin, BigDecimal sum) {
}
