package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentResponseDto;
import greencity.entity.ReplyToComment;
import org.springframework.stereotype.Component;

@Component
public class ReplyToCommentResponseMapper implements GenericCommentResponseMapper<ReplyToCommentResponseDto, ReplyToComment> {

    @Override
    public ReplyToComment toEntity(ReplyToCommentResponseDto dto) {
        return ReplyToComment.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .isEdited(dto.getIsEdited())
                .build();
    }

    @Override
    public ReplyToCommentResponseDto toDto(ReplyToComment entity) {
        return ReplyToCommentResponseDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .isEdited(entity.getIsEdited())
                .build();
    }
}
