package ru.practicum.microbank.front.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountInfo(String login, String name, LocalDate birthDay, BigDecimal balance) {
}
