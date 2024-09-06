package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentRequestDto;
import greencity.entity.ReplyToComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReplyToCommentRequestDtoMapperTest {

    private ReplyToCommentRequestDtoMapper replyToCommentRequestDtoMapper;

    @BeforeEach
    void setUp() {
        replyToCommentRequestDtoMapper = new ReplyToCommentRequestDtoMapper();
    }

    @Test
    public void testToEntity() {
        ReplyToCommentRequestDto dto = ReplyToCommentRequestDto.builder()
                .content("content")
                .isEdited(false)
                .build();

        ReplyToComment entity = replyToCommentRequestDtoMapper.toEntity(dto);

        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getIsEdited(), entity.getIsEdited());
    }

    @Test
    public void testToDto() {
        ReplyToComment entity = ReplyToComment.builder()
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();

        ReplyToCommentRequestDto dto = replyToCommentRequestDtoMapper.toDto(entity);

        assertEquals(entity.getContent(), dto.getContent());
    }
}