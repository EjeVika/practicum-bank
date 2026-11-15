package ru.practicum.microbank.notifications.dto;

public record NotificationRequest(ActionType actionType, String message) {
}
