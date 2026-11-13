package ru.practicum.microbank.front.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String login) {
        super("Не найден счет для пользователя: " + login);
    }
}
