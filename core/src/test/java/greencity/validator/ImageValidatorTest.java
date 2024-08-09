package greencity.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@DisplayName("ImageValidator Unit Tests")
class ImageValidatorTest {

    @InjectMocks
    private ImageValidator imageValidator;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private ConstraintValidatorContext context;


    // Test all valid image content types
    @Test
    @DisplayName("Valid image types should return true")
    void givenValidImageTypes_whenIsValid_thenReturnTrue() {
        String[] validTypes = {"image/jpeg", "image/png", "image/jpg"};
        for (String type : validTypes) {
            when(mockFile.getContentType()).thenReturn(type);
            boolean result = imageValidator.isValid(mockFile, context);
            System.out.println("Test with type: " + type + " resulted in: " + result);

            assertTrue(result, "Failed for type: " + type);
        }
    }

    @Test
    @DisplayName("Null image should return true")
    void givenNullImage_whenIsValid_thenReturnTrue() {
        boolean result = imageValidator.isValid(null, context);
        System.out.println("Test with null image resulted in: " + result);

        assertTrue(result, "Failed for null image");
    }

    @Test
    @DisplayName("Invalid image types should return false")
    void givenInvalidImageType_whenIsValid_thenReturnFalse() {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        boolean result = imageValidator.isValid(mockFile, context);
        System.out.println("Test with invalid content type resulted in: " + result);

        assertFalse(result, "Expected false for invalid content type");
    }

    @Test
    @DisplayName("Image with no content type should return false")
    void givenNoContentType_whenIsValid_thenReturnFalse() {
        when(mockFile.getContentType()).thenReturn(null);
        boolean result = imageValidator.isValid(mockFile, context);
        System.out.println("Test with no content type resulted in: " + result);

        assertFalse(result, "Expected false for no content type");
    }


}