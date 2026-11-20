package ru.practicum.microbank.transfer.dto;

public record NotificationResponse(String actionType, String message, String notificationResultMessage) {
}
