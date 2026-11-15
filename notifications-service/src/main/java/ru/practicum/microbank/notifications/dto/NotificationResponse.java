package ru.practicum.microbank.notifications.dto;

public record NotificationResponse(ActionType actionType, String message, String notificationResultMessage) {
}
