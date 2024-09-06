package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto save(NotificationDto notificationDto);

    List<NotificationDto> findAllByUserId(Long userId);

    List<NotificationDto> findAllByUserIdAndIsReadFalse(Long userId);

    void markAsReadNotification(Long id);

    List<NotificationDto> getFirstThreeNotifications(Long userId);

    List<NotificationDto> getNotificationsSortedByReceivedTime(Long userId, boolean ascending);
}
