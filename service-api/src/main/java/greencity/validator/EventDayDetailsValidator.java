package greencity.validator;

import greencity.annotations.ValidEventDayDetails;
import greencity.constant.ServiceValidationConstants;
import greencity.dto.event.EventDayDetailsCreatingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalTime;


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

        //Check if event day end time is before event day start time
        if (value.getEventEndTime() != null && value.getEventStartTime() != null &&
                value.getEventEndTime().isBefore(value.getEventStartTime())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ServiceValidationConstants.EVENT_TIME_RESTRICTION)
                    .addPropertyNode("eventEndTime")
                    .addConstraintViolation();
            return false;
        }

        //check if event day start time is before current time
        if (value.getEventDate() != null && value.getEventDate().isEqual(LocalDate.now())) {
            if (value.getEventStartTime().isBefore(LocalTime.now())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ServiceValidationConstants.EVENT_START_TIME_RESTRICTION)
                        .addPropertyNode("eventStartTime")
                        .addConstraintViolation();

                return false;
            }
        }

        //check if event day end time equal to event day start time
        if (value.getEventEndTime() != null && value.getEventStartTime() != null &&
                value.getEventEndTime().equals(value.getEventStartTime())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ServiceValidationConstants.EVENT_EQUAL_TIME_RESTRICTION)
                    .addPropertyNode("eventEndTime")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}