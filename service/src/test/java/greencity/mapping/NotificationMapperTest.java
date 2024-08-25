package greencity.mapping;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationSource;
import greencity.enums.NotificationSourceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationMapperTest {
    private final NotificationMapper notificationMapper = new NotificationMapper();

    @Test
    public void testToEntity_Successful() {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .section("EVENT")
                .sectionType("EVENT_EDITED")
                .text("Test notification")
                .isRead(false)
                .receivedTime("2023-10-01T10:00:00")
                .userId(1L)
                .build();

        Notification entity = this.notificationMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(NotificationSource.EVENT, entity.getSection());
        assertEquals(NotificationSourceType.EVENT_EDITED, entity.getSectionType());
        assertEquals(dto.getText(), entity.getText());
        assertEquals(dto.isRead(), entity.isRead());
        assertEquals(LocalDateTime.parse(dto.getReceivedTime()), entity.getReceivedTime());
        assertEquals(dto.getUserId(), entity.getUser().getId());
    }

    @Test
    public void testToDto_Successful() {
        User user = User.builder().id(1L).build();
        Notification entity = Notification.builder()
                .id(1L)
                .section(NotificationSource.EVENT)
                .sectionType(NotificationSourceType.EVENT_EDITED)
                .text("Test notification")
                .isRead(false)
                .receivedTime(LocalDateTime.of(2023, 10, 1, 10, 0))
                .user(user)
                .build();

        NotificationDto dto = this.notificationMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getSection().toString(), dto.getSection());
        assertEquals(entity.getSectionType().toString(), dto.getSectionType());
        assertEquals(entity.getText(), dto.getText());
        assertEquals(entity.isRead(), dto.isRead());
        assertEquals(entity.getReceivedTime().toString(), dto.getReceivedTime());
        assertEquals(entity.getUser().getId(), dto.getUserId());
    }

    @Test
    public void testToEntity_InvalidEnum() {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .section("INVALID")
                .sectionType("INVALID")
                .text("Test notification")
                .isRead(false)
                .receivedTime("2023-10-01T10:00:00")
                .userId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> notificationMapper.toEntity(dto));
    }

    @Test
    public void testToEntity_EmptyStrings() {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .section("")
                .sectionType("")
                .text("")
                .isRead(false)
                .receivedTime("2023-10-01T10:00:00")
                .userId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> notificationMapper.toEntity(dto));
    }

    @Test
    public void testToEntity_NullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> notificationMapper.toEntity(null));
        assertEquals("Cannot map null to entity", exception.getMessage());
    }

    @Test
    public void testToDto_NullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> notificationMapper.toDto(null));
        assertEquals("Cannot map null to dto", exception.getMessage());
    }
}
