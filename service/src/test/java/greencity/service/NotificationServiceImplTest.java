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
    public void testSave() {
        NotificationDto dto = new NotificationDto();
        Notification entity = new Notification();
        Notification savedEntity = new Notification();
        NotificationDto savedDto = new NotificationDto();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(notificationRepo.save(entity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(savedDto);

        NotificationDto result = notificationService.save(dto);

        assertEquals(savedDto, result);
        verify(mapper).toEntity(dto);
        verify(notificationRepo).save(entity);
        verify(mapper).toDto(savedEntity);
    }
    @Test
    public void testFindAllByUserId() {
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
    public void testMarkAsReadNotification_Success() {
        Long notificationId = 1L;
        Notification notification = new Notification();
        notification.setRead(false);

        when(notificationRepo.findById(notificationId)).thenReturn(Optional.of(notification));

        notificationService.markAsReadNotification(notificationId);

        assertTrue(notification.isRead(), "Notification should be marked as read");
        verify(notificationRepo).findById(notificationId);
        verify(notificationRepo).save(notification);
    }

    @Test
    public void testMarkAsReadNotification_NotFound() {
        Long notificationId = 1L;

        when(notificationRepo.findById(notificationId)).thenReturn(Optional.empty());

        NotificationNotFoundException exception = assertThrows(NotificationNotFoundException.class, () -> {
            notificationService.markAsReadNotification(notificationId);
        });

        String expectedMessage = "Notification with ID " + notificationId + " not found";
        assertEquals(expectedMessage, exception.getMessage());

        verify(notificationRepo).findById(notificationId);
        verify(notificationRepo, never()).save(any(Notification.class));
    }


}
