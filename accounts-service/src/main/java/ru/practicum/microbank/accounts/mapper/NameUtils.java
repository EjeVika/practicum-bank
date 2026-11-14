package ru.practicum.microbank.accounts.mapper;

public class NameUtils {
    public static String extractFirstName(String name) {
        if (name == null || name.isBlank()) return null;
        return name.trim().split("\\s+", 2)[0];
    }

    public static String extractLastName(String name) {
        if (name == null || name.isBlank()) return null;
        String[] parts = name.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : null;
    }
}
