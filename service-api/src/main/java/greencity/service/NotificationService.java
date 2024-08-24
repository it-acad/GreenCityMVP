package greencity.service;

import greencity.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService {

    NotificationDto save(NotificationDto notificationDto);

    List<NotificationDto> findAllByUserId(Long userId);

    List<NotificationDto> findAllByUserIdAndIsReadFalse(Long userId);

    void markAsReadNotification(Long id);
}
