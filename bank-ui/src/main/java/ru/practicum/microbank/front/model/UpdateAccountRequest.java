package ru.practicum.microbank.front.model;

import java.time.LocalDate;

public record UpdateAccountRequest(
        String name,
        LocalDate birthday
) {}
