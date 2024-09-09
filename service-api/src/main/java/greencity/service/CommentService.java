package greencity.service;

import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentReturnDto;

import java.util.List;

public interface CommentService {
    CommentReturnDto save(CommentDto commentDto, Long commentId, Long authorId);

    CommentReturnDto update(CommentDto commentDto, Long replyToCommentId, Long authorId);

    void deleteById(Long replyToCommentId, Long authorId);

    List<CommentReturnDto> findAllByCommentId(Long commentId);
}
