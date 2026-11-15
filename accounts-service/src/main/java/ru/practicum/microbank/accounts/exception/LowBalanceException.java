package ru.practicum.microbank.accounts.exception;

public class LowBalanceException extends RuntimeException {
    public LowBalanceException(String login) {
        super("Недостаточно средств на счете у пользователя: " + login);
    }
}
