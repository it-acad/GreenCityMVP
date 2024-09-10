package greencity.mapping;

import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.EventComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCommentResponseMapperTest {
    private EventCommentResponseMapper eventCommentResponseMapper;

    @BeforeEach
    public void setUp() {
        eventCommentResponseMapper = new EventCommentResponseMapper();
    }

    @Test
    public void toEntity_givenValidDto_shouldMapToEntity() {
        EventCommentDtoResponse dto = new EventCommentDtoResponse();
        dto.setId(1L);
        dto.setText("Sample comment");
        dto.setCreatedDate(LocalDateTime.now());

        EventComment entity = eventCommentResponseMapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getContent()).isEqualTo(dto.getText());
        assertThat(entity.getCreatedDate()).isEqualTo(dto.getCreatedDate());
    }

    @Test
    public void toEntity_givenNullDto_shouldReturnNull() {
        EventComment entity = eventCommentResponseMapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    public void toDto_givenValidEntity_shouldMapToDto() {
        EventComment entity = new EventComment();
        entity.setId(1L);
        entity.setContent("Sample comment");
        entity.setCreatedDate(LocalDateTime.now());

        EventCommentDtoResponse dto = eventCommentResponseMapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getText()).isEqualTo(entity.getContent());
        assertThat(dto.getCreatedDate()).isEqualTo(entity.getCreatedDate());
    }

    @Test
    public void toDto_givenNullEntity_shouldReturnNull() {
        EventCommentDtoResponse dto = eventCommentResponseMapper.toDto(null);

        assertThat(dto).isNull();
    }
}
