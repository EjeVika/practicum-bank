package ru.practicum.microbank.front.model;

import java.time.LocalDate;

public record CreateAccountRequest(
        String login,
        String name) {}
