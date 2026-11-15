package ru.practicum.microbank.notifications.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Strings;

public enum ActionType {
    ACCOUNT_CREATED("Создание пользователя"),
    ACCOUNT_INFO_UPDATED("Изменение данных пользователя"),
    WITHDRAW("Снятие денег со счета"),
    DEPOSIT("Пополнение счета"),
    TRANSFER("Перевод денег между счетами"),
    UNKNOWN("Неизвестное действие");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ActionType fromString(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return UNKNOWN;
        }
        String normalized = value.trim().toUpperCase();
        for (ActionType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
