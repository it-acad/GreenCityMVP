package greencity.dto.event;

import greencity.dto.user.AuthorDto;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventSendEmailDto {

    private String eventTitle;

    private String description;

    private Set<EventDayDetailsDto> eventDayDetailsList;

    private List<String> imagePathList;

    private AuthorDto author;

    private String unsubscribeToken;

}