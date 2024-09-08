package greencity.annotations;

import greencity.constant.ServiceValidationConstants;
import greencity.validator.UniqueEventDayDetailsCreationDtoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.FIELD})
@Constraint(validatedBy = UniqueEventDayDetailsCreationDtoValidator.class)
public @interface UniqueEventDayDetailsCreationDtoValidation {

    /**
     * Defines the message that will be shown when the input data is not valid.
     *
     * @return message
     */
    String message() default ServiceValidationConstants.EVENT_UNIQUE_DATE_RESTRICTION;

    /**
     * Allows you to specify validation groups to apply this constraint to.
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * Payloads are typically used to carry metadata information consumed by a
     * validation client.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
}
