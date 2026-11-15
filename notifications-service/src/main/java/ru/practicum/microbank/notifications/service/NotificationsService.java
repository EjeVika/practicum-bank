package ru.practicum.microbank.notifications.service;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.microbank.notifications.dto.NotificationRequest;
import ru.practicum.microbank.notifications.dto.NotificationResponse;

@Slf4j
@Service
public class NotificationsService {

    public NotificationResponse handleNotification(NotificationRequest request) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.actionType());
        Preconditions.checkNotNull(request.message());

        log.info("Получено уведомление о действии в системе. " +
                "Действие: \"{}\", Детали: \"{}\"", request.actionType().getDescription(), request.message());
        return new NotificationResponse(request.actionType(), request.message(),
                "Уведомление обработано успешно");
    }
}
