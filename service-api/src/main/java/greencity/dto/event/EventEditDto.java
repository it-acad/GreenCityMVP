package greencity.dto.event;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class EventEditDto {

    @Size(min = 1, max = 70, message = ServiceValidationConstants.EVENT_TITLE_LENGTH)
    private String eventTitle;
    @Size(min = 20, max = 63206, message = ServiceValidationConstants.EVENT_DESCRIPTION_LENGTH)
    private String description;
    @Size(min = 1, max = 7, message = ServiceValidationConstants.EVENT_AMOUNT_OF_DAYS)
    @Valid
    private Set<EventDayDetailsDto> eventDayDetailsList;
    private String eventType;
    private List<String> imagePathList;
}
