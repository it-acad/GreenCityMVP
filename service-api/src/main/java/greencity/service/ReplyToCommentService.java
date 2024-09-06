package greencity.service;

import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.dto.replytocomment.ReplyToCommentRequestDto;

import java.util.List;

public interface ReplyToCommentService {
    ReplyToCommentDto save(ReplyToCommentRequestDto replyToCommentDto, Long commentId, Long authorId);

    ReplyToCommentDto update(ReplyToCommentRequestDto replyToCommentDto, Long authorId);

    void deleteById(Long replyToCommentId, Long authorId);

    List<ReplyToCommentDto> findAllByCommentId(Long commentId);
}
