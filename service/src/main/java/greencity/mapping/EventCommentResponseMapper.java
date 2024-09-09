package greencity.mapping;

import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.EventComment;
import org.springframework.stereotype.Component;

@Component
public class EventCommentResponseMapper implements GenericCommentReturnMapper<EventCommentDtoResponse, EventComment> {


    @Override
    public EventComment toEntity(EventCommentDtoResponse dto) {
        if (dto == null) {
            return null;
        }
        EventComment eventComment = new EventComment();
        eventComment.setId(dto.getId());
        eventComment.setContent(dto.getText());
        eventComment.setCreatedDate(dto.getCreatedDate());
        return eventComment;
    }

    @Override
    public EventCommentDtoResponse toDto(EventComment entity) {
        if (entity == null) {
            return null;
        }
        EventCommentDtoResponse dto = new EventCommentDtoResponse();
        dto.setId(entity.getId());
        dto.setText(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}


