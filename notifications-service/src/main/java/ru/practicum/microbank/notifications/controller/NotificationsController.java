package ru.practicum.microbank.notifications.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Slf4j
public class NotificationsController {

    @PostMapping("/notify")
    @PreAuthorize("hasRole('USER') && hasAuthority('notifications.access')")
    public String handleNotification(@RequestBody NotificationRequest request) {
        return "OK";
    }
}
