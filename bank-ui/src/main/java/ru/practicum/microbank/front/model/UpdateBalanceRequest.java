package ru.practicum.microbank.front.model;

import java.math.BigDecimal;

public record UpdateBalanceRequest(BigDecimal sum, CashAction action) {
}