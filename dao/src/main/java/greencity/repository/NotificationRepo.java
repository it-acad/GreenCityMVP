package greencity.repository;

import greencity.entity.Notification;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserId(Long userId);
    List<Notification> findAllByUserIdAndIsReadFalse(Long userId);
}
