package greencity.validator;

import greencity.annotations.ImageListSizeValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public class ImageListSizeValidator implements ConstraintValidator<ImageListSizeValidation, List<MultipartFile>> {

    private int maxSize;

    @Override
    public void initialize(ImageListSizeValidation constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext context) {
        if (images == null) {
            // If the list is null, consider it valid
            return true;
        }
        // Check if the size of the images list is less than or equal to the maximum allowed size
        return images.size() <= maxSize;
    }

}