package greencity.service;

import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;

import java.util.List;

public interface EventCommentService {

    EventCommentDtoResponse saveReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId);

    AddEventCommentDtoResponse addComment(Long eventId, AddEventCommentDtoRequest commentDto, UserVO currentUser);

    EventCommentDtoResponse updateReply(EventCommentDtoRequest commentDtoRequest, Long replyToCommentId, Long authorId);

    List<AddEventCommentDtoResponse> getCommentsByEventId(Long eventId);

    void deleteReplyById(Long replyToCommentId, Long authorId);

    List<EventCommentDtoResponse> findAllReplyByCommentId(Long commentId);

    String filterText(String input , String userName);

    Long showQuantityOfAddedComments(Long eventId);
}
