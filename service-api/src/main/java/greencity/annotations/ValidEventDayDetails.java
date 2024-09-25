package greencity.annotations;

import greencity.validator.EventDayDetailsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = EventDayDetailsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.FIELD})
public @interface ValidEventDayDetails {
    /**
     * Defines the message that will be shown when the input data is not valid.
     *
     * @return message
     */
    String message() default "Online or offline event must be selected, and the corresponding place fields must be filled.";

    /**
     * Allows you to specify validation groups to apply this constraint to.
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * Payloads are typically used to carry metadata information consumed by a validation client.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

}