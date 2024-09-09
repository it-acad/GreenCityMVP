package greencity.mapping;

import greencity.dto.comment.CommentDto;
import greencity.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements GenericCommentMapper<CommentDto, Comment> {

    @Override
    public Comment toEntity(CommentDto dto) {
        if (dto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setText(dto.getText());
        return comment;
    }

    @Override
    public CommentDto toDto(Comment entity) {
        if (entity == null) {
            return null;
        }
        CommentDto dto = new CommentDto();
        dto.setText(entity.getText());
        return dto;
    }
}
