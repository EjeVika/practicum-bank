package ru.practicum.microbank.accounts.dto;

public record NotificationResponse(String actionType, String message, String notificationResultMessage) {
}
