package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.ReplyToComment;
import org.springframework.stereotype.Component;

@Component
public class ReplyToCommentMapper implements GenericCommentMapper<ReplyToCommentDto, ReplyToComment> {

    @Override
    public ReplyToComment toEntity(ReplyToCommentDto dto) {
        return ReplyToComment.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .createdDate(dto.getCreatedDate())
                .isEdited(dto.getIsEdited())
                .build();
    }

    @Override
    public ReplyToCommentDto toDto(ReplyToComment entity) {
        return ReplyToCommentDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .isEdited(entity.getIsEdited())
                .build();
    }
}
