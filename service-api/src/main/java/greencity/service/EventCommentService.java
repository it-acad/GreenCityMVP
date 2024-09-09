package greencity.service;

import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;

import java.util.List;

public interface EventCommentService {
    AddEventCommentDtoResponse addComment(Long eventId, AddEventCommentDtoRequest commentDto, UserVO currentUser);

    List<AddEventCommentDtoResponse> getCommentsByEventId(Long eventId);

    AddEventCommentDtoResponse replyToComment(Long eventId, Long parentCommentId, AddEventCommentDtoRequest replyDto
            , UserVO currentUserVO);

    String filterText(String input , String userName);

    Long showQuantityOfAddedComments(Long eventId);
}
