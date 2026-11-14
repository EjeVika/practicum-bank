package ru.practicum.microbank.cash.dto;

import java.math.BigDecimal;

public record UpdateBalanceRequest(BigDecimal sum, CashAction action) {
}
