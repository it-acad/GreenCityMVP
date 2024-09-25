package greencity.dto.event;

import greencity.annotations.UniqueEventDayDetailsCreationDtoValidation;
import greencity.annotations.ValidEventDayDetails;
import greencity.constant.ServiceValidationConstants;
import greencity.dto.user.AuthorDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventCreationDtoRequest {
    @Size(min = 1, max = 70, message = ServiceValidationConstants.EVENT_TITLE_LENGTH)
    private String eventTitle;

    @Size(min = 20, max = 63206, message = ServiceValidationConstants.EVENT_DESCRIPTION_LENGTH)
    private String description;

    @Valid
    @Size(min = 1, max = 7, message = ServiceValidationConstants.EVENT_AMOUNT_OF_DAYS)
    @UniqueEventDayDetailsCreationDtoValidation
    private Set<@ValidEventDayDetails EventDayDetailsCreatingDto> eventDayDetailsList;

    private String eventType;

    private AuthorDto author;

}
