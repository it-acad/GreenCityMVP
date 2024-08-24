package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;

    @Override
    public NotificationDto save(NotificationDto notificationDto) {
        return null;
    }

    @Override
    public List<NotificationDto> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<NotificationDto> findAllByUserIdAndIsReadFalse(Long userId) {
        return List.of();
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(); // TODO: add exception

        notification.setRead(true);
        notificationRepo.save(notification);
    }
}
