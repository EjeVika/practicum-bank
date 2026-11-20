package ru.practicum.microbank.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountInfo(String login, String name, LocalDate birthday, BigDecimal balance) {
}
