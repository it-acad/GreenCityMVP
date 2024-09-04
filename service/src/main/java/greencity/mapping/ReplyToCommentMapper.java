package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.ReplyToComment;
import org.modelmapper.AbstractConverter;

public class ReplyToCommentMapper extends AbstractConverter<ReplyToComment, ReplyToCommentDto> {
    @Override
    protected ReplyToCommentDto convert(ReplyToComment replyToComment) {
        return ReplyToCommentDto.builder()
                .id(replyToComment.getId())
                .content(replyToComment.getContent())
                .createdDate(replyToComment.getCreatedDate())
                .isEdited(replyToComment.getIsEdited())
                .build();
    }
}
