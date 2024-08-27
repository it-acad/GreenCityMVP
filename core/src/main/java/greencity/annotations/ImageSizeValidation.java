package greencity.annotations;

import greencity.validator.ImageSizeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageSizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ImageSizeValidation {
    /**
     * Defines the maximum allowed size for the image in megabytes.
     *
     * @return the maximum size in MB
     */
    int maxSizeMB();

    /**
     * Defines the message that will be shown when the input data is not valid.
     *
     * @return the error message
     */
    String message() default "Image size exceeds the maximum allowed size.";

    /**
     * Allows you to specify the validation groups to apply this constraint to.
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