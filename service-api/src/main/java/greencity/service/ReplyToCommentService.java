package greencity.service;

import greencity.dto.replytocomment.ReplyToCommentDto;

import java.util.List;

public interface ReplyToCommentService {
    ReplyToCommentDto save(ReplyToCommentDto replyToCommentDto, Long commentId, Long authorId);

    ReplyToCommentDto update(ReplyToCommentDto replyToCommentDto, Long authorId);

    void deleteById(Long replyToCommentId, Long authorId);

    List<ReplyToCommentDto> findAllByCommentId(Long commentId);
}
