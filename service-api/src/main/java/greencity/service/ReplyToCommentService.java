package greencity.service;

import greencity.dto.replytocomment.ReplyToCommentResponseDto;
import greencity.dto.replytocomment.ReplyToCommentRequestDto;

import java.util.List;

public interface ReplyToCommentService {
    ReplyToCommentResponseDto save(ReplyToCommentRequestDto replyToCommentDto, Long commentId, Long authorId);

    ReplyToCommentResponseDto update(ReplyToCommentRequestDto replyToCommentDto, Long replyToCommentId, Long authorId);

    void deleteById(Long replyToCommentId, Long authorId);

    List<ReplyToCommentResponseDto> findAllByCommentId(Long commentId);
}
