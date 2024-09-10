package greencity.service;

import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;

import java.util.List;

public interface EventCommentService {
    EventCommentDtoResponse saveReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId);

    EventCommentDtoResponse updateReply(EventCommentDtoRequest commentDtoRequest, Long replyToCommentId, Long authorId);

    void deleteReplyById(Long replyToCommentId, Long authorId);

    List<EventCommentDtoResponse> findAllReplyByCommentId(Long commentId);
}
