package greencity.dto.event;

import greencity.enums.EventLine;
import greencity.enums.EventTime;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventFilterDto {

    private EventLine eventLine;             // ONLINE or OFFLINE

    private String eventLocation;            // City name

    private EventTime eventTime;             // FUTURE, PAST, or LIVE

}