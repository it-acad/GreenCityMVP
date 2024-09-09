package greencity.service;

import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;

import java.util.List;

public interface EventCommentService {
    EventCommentDtoResponse save(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId);

    EventCommentDtoResponse update(EventCommentDtoRequest commentDtoRequest, Long replyToCommentId, Long authorId);

    void deleteById(Long replyToCommentId, Long authorId);

    List<EventCommentDtoResponse> findAllByCommentId(Long commentId);
}
