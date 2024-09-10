package greencity.mapping;

import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.entity.EventComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCommentDtoRequestMapperTest {

    private EventCommentDtoRequestMapper eventCommentDtoRequestMapper;

    @BeforeEach
    public void setup() {
        eventCommentDtoRequestMapper = new EventCommentDtoRequestMapper();
    }

    @Test
    public void toEntity_givenValidDto_shouldMapToEntity() {
        EventCommentDtoRequest dto = new EventCommentDtoRequest();
        dto.setText("Sample comment");

        EventComment entity = eventCommentDtoRequestMapper.toEntity(dto);

        assertThat(entity.getContent()).isEqualTo(dto.getText());
    }

    @Test
    public void toEntity_givenNullDto_shouldReturnNull() {
        EventComment entity = eventCommentDtoRequestMapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    public void toDto_givenValidEntity_shouldMapToDto() {
        EventComment entity = new EventComment();
        entity.setContent("Sample comment");

        EventCommentDtoRequest dto = eventCommentDtoRequestMapper.toDto(entity);

        assertThat(dto.getText()).isEqualTo(entity.getContent());
    }

    @Test
    public void toDto_givenNullEntity_shouldReturnNull() {
        EventCommentDtoRequest dto = eventCommentDtoRequestMapper.toDto(null);

        assertThat(dto).isNull();
    }
}

