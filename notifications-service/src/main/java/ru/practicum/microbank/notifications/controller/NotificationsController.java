package ru.practicum.microbank.notifications.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.microbank.notifications.dto.NotificationRequest;
import ru.practicum.microbank.notifications.dto.NotificationResponse;
import ru.practicum.microbank.notifications.service.NotificationsService;

@RestController
@RequestMapping("/")
public class NotificationsController {

    private final NotificationsService notificationsService;

    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @PostMapping("/notify")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('notifications.access')")
    public NotificationResponse handleNotification(@RequestBody NotificationRequest request) {
        return notificationsService.handleNotification(request);
    }
}
