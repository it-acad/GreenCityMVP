package greencity.annotations;

import greencity.validator.ImageListSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageListSizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ImageListSizeValidation {
    /**
     * Defines the maximum allowed size for the image list.
     *
     * @return the maximum size
     */
    int maxSize();

    /**
     * Defines the message that will be shown when the input data is not valid.
     * The `{maxSize}` placeholder will be replaced with the actual max size value.
     *
     * @return message
     */
    String message() default "The number of images exceeds the maximum allowed limit of {maxSize} images.";

    /**
     * Allows you to specify the validation groups to apply this constraint to.
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