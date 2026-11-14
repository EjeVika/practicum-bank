package ru.practicum.microbank.accounts.dto;

import java.math.BigDecimal;

public record UpdateBalanceRequest(BigDecimal sum, CashAction action) {
}
