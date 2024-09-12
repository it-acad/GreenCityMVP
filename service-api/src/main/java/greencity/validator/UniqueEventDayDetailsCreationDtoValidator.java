package greencity.validator;

import greencity.annotations.UniqueEventDayDetailsCreationDtoValidation;
import greencity.dto.event.EventDayDetailsCreatingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UniqueEventDayDetailsCreationDtoValidator implements ConstraintValidator<UniqueEventDayDetailsCreationDtoValidation, Set<EventDayDetailsCreatingDto>> {
    @Override
    public void initialize(UniqueEventDayDetailsCreationDtoValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(Set<EventDayDetailsCreatingDto> eventDayDetailsCreatingDtos, ConstraintValidatorContext constraintValidatorContext) {
        Set<LocalDate> eventDates = new HashSet<>();

        for (EventDayDetailsCreatingDto eventDayDetail : eventDayDetailsCreatingDtos) {
            if (!eventDates.add(eventDayDetail.getEventDate())) {
                return false;
            }
        }

        return true;
    }
}
