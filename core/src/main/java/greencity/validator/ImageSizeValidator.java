package greencity.validator;

import greencity.annotations.ImageSizeValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;


public class ImageSizeValidator implements ConstraintValidator<ImageSizeValidation, MultipartFile> {

    private long maxSizeInBytes;

    @Override
    public void initialize(ImageSizeValidation constraintAnnotation) {
        // Convert megabytes to bytes for comparison
        this.maxSizeInBytes = constraintAnnotation.maxSizeMB() * 1024L * 1024L;
    }

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext context) {
        if (image == null) {
            // If the image is null, we consider it valid (no file provided).
            return true;
        }
        // Validate the file size against the maximum allowed size in bytes.
        return image.getSize() <= maxSizeInBytes;
    }

}