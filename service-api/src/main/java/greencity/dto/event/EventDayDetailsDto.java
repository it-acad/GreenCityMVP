package greencity.dto.event;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.LocalTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventDayDetailsDto {

    private Long id;

    @NotNull
    @Future(message = ServiceValidationConstants.EVENT_DAY_RESTRICTION)
    private LocalDate eventDate;

    @PastOrPresent(message = ServiceValidationConstants.EVENT_TIME_RESTRICTION)
    @Future(message = ServiceValidationConstants.EVENT_TIME_RESTRICTION)
    private LocalTime eventStartTime;

    private LocalTime eventEndTime;

    private boolean isAllDateDuration;

    private boolean isOnline;

    @URL
    private String onlinePlace;

    private boolean isOffline;

    private String offlinePlace;

    @Min(value = -90, message = "Latitude must be between -90 and 90 degrees")
    @Max(value = 90, message = "Latitude must be between -90 and 90 degrees")
    private double latitude;

    @Min(value = -180, message = "Longitude must be between -180 and 180 degrees")
    @Max(value = 180, message = "Longitude must be between -180 and 180 degrees")
    private double longitude;

}
