package greencity.mapping;

import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventCommentResponseMapperTest {
    private EventCommentResponseMapper eventCommentResponseMapper;

    @BeforeEach
    public void setUp() {
        eventCommentResponseMapper = new EventCommentResponseMapper();
    }

    @Test
    public void testToEntity_WhenDtoIsNull_ShouldReturnNull() {
        EventCommentDtoResponse dto = null;
        EventComment entity = eventCommentResponseMapper.toEntity(dto);
        assertNull(entity);
    }

    @Test
    public void testToEntity_WhenDtoIsValid_ShouldMapToEntity() {
        EventCommentDtoResponse dto = new EventCommentDtoResponse();
        dto.setId(1L);
        dto.setEventId(2L);
        dto.setUserId(3L);
        dto.setUserName("John Doe");
        dto.setText("Test comment");
        dto.setCreatedDate(LocalDateTime.now());
        dto.setEdited(true);

        EventComment entity = eventCommentResponseMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getEventId(), entity.getEvent().getId());
        assertEquals(dto.getUserId(), entity.getAuthor().getId());
        assertEquals(dto.getUserName(), entity.getAuthor().getName());
        assertEquals(dto.getText(), entity.getContent());
        assertEquals(dto.getCreatedDate(), entity.getCreatedDate());
        assertEquals(dto.isEdited(), entity.getIsEdited());
    }

    @Test
    public void testToDto_WhenEntityIsNull_ShouldReturnNull() {
        EventComment entity = null;
        EventCommentDtoResponse dto = eventCommentResponseMapper.toDto(entity);
        assertNull(dto);
    }

    @Test
    public void testToDto_WhenEntityIsValid_ShouldMapToDto() {
        User user = User.builder()
                .id(3L)
                .name("John Doe")
                .build();

        Event event = Event.builder()
                .id(2L)
                .build();

        EventComment entity = new EventComment();
        entity.setId(1L);
        entity.setAuthor(user);
        entity.setEvent(event);
        entity.setContent("Test comment");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setIsEdited(true);

        EventCommentDtoResponse dto = eventCommentResponseMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getEvent().getId(), dto.getEventId());
        assertEquals(entity.getAuthor().getId(), dto.getUserId());
        assertEquals(entity.getAuthor().getName(), dto.getUserName());
        assertEquals(entity.getContent(), dto.getText());
        assertEquals(entity.getCreatedDate(), dto.getCreatedDate());
        assertEquals(entity.getIsEdited(), dto.isEdited());
    }
}
