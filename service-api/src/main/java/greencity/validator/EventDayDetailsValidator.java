package greencity.validator;

import greencity.annotations.ValidEventDayDetails;
import greencity.dto.event.EventDayDetailsCreatingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class EventDayDetailsValidator implements ConstraintValidator<ValidEventDayDetails, EventDayDetailsCreatingDto> {

    @Override
    public void initialize(ValidEventDayDetails constraintAnnotation) {
        // Initializes the validator in preparation for #isValid calls
    }

    @Override
    public boolean isValid(EventDayDetailsCreatingDto value, ConstraintValidatorContext context) {
        boolean isOnline = value.isOnline();
        boolean isOffline = value.isOffline();
        String onlinePlace = value.getOnlinePlace();
        String offlinePlace = value.getOfflinePlace();

        // Check if both isOnline and isOffline are false
        if (!isOnline && !isOffline) {
            return false;
        }

        // Check if isOnline is true but onlinePlace is empty
        if (isOnline && (onlinePlace == null || onlinePlace.isEmpty())) {
            return false;
        }

        // Check if isOffline is true but offlinePlace is empty
        if (isOffline && (offlinePlace == null || offlinePlace.isEmpty())) {
            return false;
        }

        return true;
    }

}