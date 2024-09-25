package greencity.dto.event;

import greencity.dto.user.PlaceAuthorDto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventCommentNotificationDto {
    private String eventTitle;
    private String commentText;
    private String commentAuthor;
    private String commentDate;
    private PlaceAuthorDto author;
    private String secureToken;
}
