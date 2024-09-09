package greencity.mapping;

import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentReturnMapper implements GenericCommentReturnMapper<CommentReturnDto, Comment> {

    @Override
    public Comment toEntity(CommentReturnDto dto) {
        if (dto == null) {
            return null;
        }
        return Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .createdDate(dto.getCreatedDate())
                .build();
    }

    @Override
    public CommentReturnDto toDto(Comment entity) {
        if (entity == null) {
            return null;
        }
        CommentReturnDto dto = new CommentReturnDto();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}

