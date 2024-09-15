package greencity.service;

import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;

import java.util.List;

public interface EventCommentService {
    AddEventCommentDtoResponse addComment(Long eventId, AddEventCommentDtoRequest commentDto, UserVO currentUser);

    List<AddEventCommentDtoResponse> getCommentsByEventId(Long eventId);

    String filterText(String input, String userName);

    Long showQuantityOfAddedComments(Long eventId);

    AddEventCommentDtoResponse getCommentById(Long commentId);

    void deleteCommentById(Long eventId, Long commentId, UserVO currentUser);
}
