package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.exception.exceptions.NotificationNotFoundException;
import greencity.mapping.NotificationMapper;
import greencity.repository.NotificationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepo notificationRepo;

    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    public void testFindAllByUserId_ValidUserId() {
        Long userId = 1L;
        List<Notification> notifications = List.of(new Notification());
        List<NotificationDto> notificationDtos = List.of(new NotificationDto());

        when(notificationRepo.findAllByUserId(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.findAllByUserId(userId);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserId(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testFindAllByUserId_InvalidUserId() {
        Long userId = -1L;
        List<Notification> notifications = List.of();
        when(notificationRepo.findAllByUserId(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.findAllByUserId(userId);

        assertTrue(result.isEmpty());
        verify(notificationRepo).findAllByUserId(userId);
    }

    @Test
    public void testFindAllByUserId_NoNotifications() {
        Long userId = 1L;
        List<Notification> notifications = List.of();
        when(notificationRepo.findAllByUserId(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.findAllByUserId(userId);

        assertTrue(result.isEmpty());
        verify(notificationRepo).findAllByUserId(userId);
    }

    @Test
    public void testFindAllByUserIdAndIsReadFalse() {
        Long userId = 1L;
        List<Notification> notifications = List.of(new Notification());
        List<NotificationDto> notificationDtos = List.of(new NotificationDto());

        when(notificationRepo.findAllByUserIdAndIsReadFalse(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.findAllByUserIdAndIsReadFalse(userId);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserIdAndIsReadFalse(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testFindAllByUserIdAndIsReadFalse_ValidUserId() {
        Long userId = 1L;
        List<Notification> notifications = List.of(new Notification());
        when(notificationRepo.findAllByUserIdAndIsReadFalse(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.findAllByUserIdAndIsReadFalse(userId);

        assertEquals(notifications.size(), result.size());
        verify(notificationRepo).findAllByUserIdAndIsReadFalse(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testFindAllByUserIdAndIsReadFalse_InvalidUserId() {
        Long userId = -1L;
        List<Notification> notifications = List.of();
        when(notificationRepo.findAllByUserIdAndIsReadFalse(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.findAllByUserIdAndIsReadFalse(userId);

        assertTrue(result.isEmpty());
        verify(notificationRepo).findAllByUserIdAndIsReadFalse(userId);
    }

    @Test
    public void testFindAllByUserIdAndIsReadFalse_NoUnreadNotifications() {
        Long userId = 1L;
        List<Notification> notifications = List.of();
        when(notificationRepo.findAllByUserIdAndIsReadFalse(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.findAllByUserIdAndIsReadFalse(userId);

        assertTrue(result.isEmpty());
        verify(notificationRepo).findAllByUserIdAndIsReadFalse(userId);
    }

    @Test
    public void testMarkAsReadNotification_Success() {
        Long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setRead(false);
        notification.setReceivedTime(LocalDateTime.now().minusDays(1));

        when(notificationRepo.findById(notificationId)).thenReturn(Optional.of(notification));

        notificationService.markAsReadNotification(notificationId);

        assertTrue(notification.isRead());
        assertNotNull(notification.getReceivedTime());
        verify(notificationRepo).findById(notificationId);
    }

    @Test
    public void testMarkAsReadNotification_NotificationNotFound() {
        Long notificationId = 1L;
        when(notificationRepo.findById(notificationId)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsReadNotification(notificationId));
        verify(notificationRepo).findById(notificationId);
    }

    @Test
    public void testGetFirstThreeNotifications_Success() {
        Long userId = 1L;
        List<Notification> notifications = List.of(new Notification(), new Notification(), new Notification());
        List<NotificationDto> notificationDtos = List.of(new NotificationDto(), new NotificationDto(), new NotificationDto());

        when(notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto(), new NotificationDto(), new NotificationDto());

        List<NotificationDto> result = notificationService.getFirstThreeNotifications(userId);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findFirstThreeByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testGetFirstThreeNotifications_NoNotifications() {
        Long userId = 1L;
        List<Notification> notifications = List.of();

        when(notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.getFirstThreeNotifications(userId);

        assertTrue(result.isEmpty());
        verify(notificationRepo).findFirstThreeByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, never()).toDto(any(Notification.class));
    }

    @Test
    public void testGetFirstThreeNotifications_OneNotification() {
        Long userId = 1L;
        Notification notification = new Notification();
        NotificationDto notificationDto = new NotificationDto();
        List<Notification> notifications = List.of(notification);
        List<NotificationDto> notificationDtos = List.of(notificationDto);

        when(notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(notification)).thenReturn(notificationDto);

        List<NotificationDto> result = notificationService.getFirstThreeNotifications(userId);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findFirstThreeByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper).toDto(notification);
    }

    @Test
    public void testGetNotificationsSortedByReceivedTime_Ascending_Success() {
        Long userId = 1L;
        boolean ascending = true;
        List<Notification> notifications = List.of(
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(2)),
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(1)),
                new Notification().setReceivedTime(LocalDateTime.now())
        );

        List<NotificationDto> notificationDtos = List.of(new NotificationDto(), new NotificationDto(), new NotificationDto());

        when(notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.getNotificationsSortedByReceivedTime(userId, ascending);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testGetNotificationsSortedByReceivedTime_Descending_Success() {
        Long userId = 1L;
        boolean descending = false;
        List<Notification> notifications = List.of(
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(2)),
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(1)),
                new Notification().setReceivedTime(LocalDateTime.now())
        );

        List<NotificationDto> notificationDtos = List.of(new NotificationDto(), new NotificationDto(), new NotificationDto());

        when(notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.getNotificationsSortedByReceivedTime(userId, descending);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testGetNotificationsSortedByReceivedTime_SameReceivedTime() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = List.of(
                new Notification().setReceivedTime(now),
                new Notification().setReceivedTime(now),
                new Notification().setReceivedTime(now)
        );
        List<NotificationDto> notificationDtos = List.of(new NotificationDto(), new NotificationDto(), new NotificationDto());

        when(notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.getNotificationsSortedByReceivedTime(userId, true);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testGetNotificationsSortedByReceivedTime_MixedReceivedTime() {
        Long userId = 1L;
        List<Notification> notifications = List.of(
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(3)),
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(1)),
                new Notification().setReceivedTime(LocalDateTime.now().minusDays(2))
        );
        List<NotificationDto> notificationDtos = List.of(new NotificationDto(), new NotificationDto(), new NotificationDto());

        when(notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.getNotificationsSortedByReceivedTime(userId, true);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }

    @Test
    public void testGetNotificationsSortedByReceivedTime_InvalidUserId() {
        Long userId = -1L;
        List<Notification> notifications = List.of();

        when(notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.getNotificationsSortedByReceivedTime(userId, true);

        assertTrue(result.isEmpty());
        verify(notificationRepo).findAllByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, never()).toDto(any(Notification.class));
    }

    @Test
    public void testGetNotificationsSortedByReceivedTime_LargeNumberOfEntries() {
        Long userId = 1L;
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            notifications.add(new Notification().setReceivedTime(LocalDateTime.now().minusDays(i)));
        }
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(n -> new NotificationDto())
                .toList();

        when(notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId)).thenReturn(notifications);
        when(mapper.toDto(any(Notification.class))).thenReturn(new NotificationDto());

        List<NotificationDto> result = notificationService.getNotificationsSortedByReceivedTime(userId, true);

        assertEquals(notificationDtos.size(), result.size());
        verify(notificationRepo).findAllByUserIdOrderByReceivedTimeDesc(userId);
        verify(mapper, times(notifications.size())).toDto(any(Notification.class));
    }
}
