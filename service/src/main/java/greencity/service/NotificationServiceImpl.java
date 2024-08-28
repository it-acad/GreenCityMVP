package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.InvalidUserIdException;
import greencity.exception.exceptions.NotificationNotFoundException;
import greencity.mapping.NotificationMapper;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotificationMapper mapper;

    @Override
    @Transactional
    public NotificationDto save(NotificationDto notificationDto) {
        Notification mappedNotification = mapper.toEntity(notificationDto);
        Notification savedNotification = notificationRepo.save(mappedNotification);
        return mapper.toDto(savedNotification);
    }

    @Override
    public List<NotificationDto> findAllByUserId(Long userId) {
        List<Notification> notifications = notificationRepo.findAllByUserId(userId);
        return notifications.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<NotificationDto> findAllByUserIdAndIsReadFalse(Long userId) {
        if (userId == null) {
            throw new InvalidUserIdException("User ID cannot be null");
        }
        List<Notification> notifications = notificationRepo.findAllByUserIdAndIsReadFalse(userId);
        if (notifications.isEmpty()){
            return Collections.emptyList();
        }
        return notifications.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void markAsReadNotification(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + id + " not found"));

        if (notification.isRead()){
            throw new BadRequestException(ErrorMessage.NOTIFICATION_ALREADY_READ);
        }
            notification.setRead(true);
            notification.setReceivedTime(LocalDateTime.now());
    }

    @Override
    public List<NotificationDto> getFirstThreeNotifications(Long userId) {
        List<Notification> notifications = notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(userId);
        return notifications.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<NotificationDto> getNotificationsSortedByReceivedTime(Long userId, boolean ascending) {
        List<Notification> notifications = notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId);
        Comparator<Notification> comparator = Comparator.comparing(Notification::getReceivedTime);

        if (!ascending) {
            comparator = comparator.reversed();
        }
        return notifications.stream()
                .sorted(comparator)
                .map(mapper::toDto)
                .toList();
    }
}
