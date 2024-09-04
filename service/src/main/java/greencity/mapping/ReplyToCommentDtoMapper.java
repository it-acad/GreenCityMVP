package greencity.mapping;

import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.ReplyToComment;
import org.modelmapper.AbstractConverter;

public class ReplyToCommentDtoMapper extends AbstractConverter<ReplyToCommentDto, ReplyToComment> {
    @Override
    protected ReplyToComment convert(ReplyToCommentDto replyToCommentDto) {
        return ReplyToComment.builder()
                .id(replyToCommentDto.getId())
                .content(replyToCommentDto.getContent())
                .createdDate(replyToCommentDto.getCreatedDate())
                .isEdited(replyToCommentDto.getIsEdited())
                .build();
    }
}
