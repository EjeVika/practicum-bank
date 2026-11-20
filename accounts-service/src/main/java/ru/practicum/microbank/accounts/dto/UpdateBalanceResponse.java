package ru.practicum.microbank.accounts.dto;

import java.math.BigDecimal;

public record UpdateBalanceResponse(String login, BigDecimal resultBalance, BigDecimal sum, CashAction action, String message) {
}