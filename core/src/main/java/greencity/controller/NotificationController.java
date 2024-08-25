package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@CurrentUser UserVO currentUser) {
        List<NotificationDto> notifications = notificationService.findAllByUserIdAndIsReadFalse(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDto>> getAllNotifications(@CurrentUser UserVO currentUser) {
        List<NotificationDto> notifications = notificationService.findAllByUserId(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/markAsViewed/{id}")
    public ResponseEntity<Void> markAsViewed(@PathVariable Long id) {
        notificationService.markAsReadNotification(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<NotificationDto>> getFirstThreeNotifications(@CurrentUser UserVO currentUser) {
        List<NotificationDto> notifications = notificationService.getFirstThreeNotifications(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<NotificationDto>> getNotificationsSortedByReceivedTime(@CurrentUser UserVO currentUser,
                                                                                      @RequestParam boolean ascending) {
        List<NotificationDto> notifications = notificationService.getNotificationsSortedByReceivedTime(currentUser.getId(), ascending);
        return ResponseEntity.ok(notifications);
    }
}
