package ru.practicum.microbank.cash.dto;

public record NotificationResponse(String actionType, String message, String notificationResultMessage) {
}
