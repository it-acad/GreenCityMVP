package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentRequestDto;
import greencity.entity.ReplyToComment;
import org.springframework.stereotype.Component;

@Component
public class ReplyToCommentRequestDtoMapper implements GenericCommentRequestMapper<ReplyToCommentRequestDto, ReplyToComment> {
    @Override
    public ReplyToComment toEntity(ReplyToCommentRequestDto dto) {
        return ReplyToComment.builder()
                .content(dto.getContent())
                .build();
    }

    @Override
    public ReplyToCommentRequestDto toDto(ReplyToComment entity) {
        return ReplyToCommentRequestDto.builder()
                .content(entity.getContent())
                .build();
    }
}
