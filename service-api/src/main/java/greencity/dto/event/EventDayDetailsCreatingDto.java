package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.constant.ServiceValidationConstants;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventDayDetailsCreatingDto {

    @NotNull
    @Future(message = ServiceValidationConstants.EVENT_DAY_RESTRICTION)
    private LocalDate eventDate;

    @PastOrPresent(message = ServiceValidationConstants.EVENT_TIME_RESTRICTION)
    @Future(message = ServiceValidationConstants.EVENT_TIME_RESTRICTION)
    private LocalTime eventStartTime;

    private LocalTime eventEndTime;

    @JsonProperty("isAllDateDuration")
    private boolean isAllDateDuration;

    @JsonProperty("isOnline")
    private boolean isOnline;

    @JsonProperty("isOffline")
    private boolean isOffline;

    private String offlinePlace;

    private String onlinePlace;

    @Min(value = -90, message = "Latitude must be between -90 and 90 degrees")
    @Max(value = 90, message = "Latitude must be between -90 and 90 degrees")
    private double latitude;

    @Min(value = -180, message = "Longitude must be between -180 and 180 degrees")
    @Max(value = 180, message = "Longitude must be between -180 and 180 degrees")
    private double longitude;

}
