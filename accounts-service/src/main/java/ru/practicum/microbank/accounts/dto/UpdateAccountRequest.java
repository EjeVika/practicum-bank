package ru.practicum.microbank.accounts.dto;

import java.time.LocalDate;

public record UpdateAccountRequest(
        String name,
        LocalDate birthday
) {
}
