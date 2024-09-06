package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.ReplyToComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReplyToCommentMapperTest {
    private ReplyToCommentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ReplyToCommentMapper();
    }

    @Test
    void toEntityTest() {
        ReplyToCommentDto dto = ReplyToCommentDto.builder()
                .id(1L)
                .content("Test content")
                .createdDate(LocalDateTime.now())
                .isEdited(false)
                .build();

        ReplyToComment entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getCreatedDate(), entity.getCreatedDate());
        assertEquals(dto.getIsEdited(), entity.getIsEdited());
    }

    @Test
    void toDtoTest() {
        ReplyToComment entity = ReplyToComment.builder()
                .id(1L)
                .content("Test content")
                .createdDate(LocalDateTime.now())
                .isEdited(false)
                .build();

        ReplyToCommentDto dto = mapper.toDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getCreatedDate(), dto.getCreatedDate());
        assertEquals(entity.getIsEdited(), dto.getIsEdited());
    }


    @Test
    void toEntityWithPartialValuesTest() {
        ReplyToCommentDto dto = ReplyToCommentDto.builder()
                .id(1L)
                .content("Partial content")
                .build();

        ReplyToComment entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getContent(), entity.getContent());
        assertNull(entity.getCreatedDate());
        assertNull(entity.getIsEdited());
    }

}
