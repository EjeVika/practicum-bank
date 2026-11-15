package ru.practicum.microbank.front.model;

import java.math.BigDecimal;

public record UpdateBalanceResponse(String login, BigDecimal resultBalance, BigDecimal sum, CashAction action, String message) {
}