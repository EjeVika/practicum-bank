package ru.practicum.microbank.accounts.mapper;

import com.google.common.base.Strings;
import ru.practicum.microbank.accounts.model.User;

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

    public static String combineName(User user) {
        if (user == null) {
            return null;
        }
        return combineName(user.getFirstName(), user.getLastName());
    }

    public static String combineName(String firstName, String lastName) {
        if (Strings.isNullOrEmpty(firstName) && Strings.isNullOrEmpty(lastName)) {
            return null;
        }
        if (Strings.isNullOrEmpty(firstName)) {
            return lastName;
        }
        if (Strings.isNullOrEmpty(lastName)) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
