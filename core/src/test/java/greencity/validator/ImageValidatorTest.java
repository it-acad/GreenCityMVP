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

    @Test
    @DisplayName("Valid image types should return true")
    void isValid_ValidImageTypes_ReturnsTrue() {
        String[] validTypes = {"image/jpeg", "image/png", "image/jpg"};
        for (String type : validTypes) {
            when(mockFile.getContentType()).thenReturn(type);

            assertTrue(imageValidator.isValid(mockFile, context), "Failed for type: " + type);
        }
    }

    @Test
    @DisplayName("Null image should return true")
    void isValid_NullImage_ReturnsTrue() {

        assertTrue(imageValidator.isValid(null, context), "Expected true for null image");
    }

    @Test
    @DisplayName("BMP image type should return false")
    void isValid_BmpImageType_ReturnsFalse() {
        when(mockFile.getContentType()).thenReturn("image/bmp");

        assertFalse(imageValidator.isValid(mockFile, context), "Expected false for type: image/bmp");
    }

    @Test
    @DisplayName("Invalid image types should return false")
    void isValid_InvalidImageType_ReturnsFalse() {
        when(mockFile.getContentType()).thenReturn("application/pdf");

        assertFalse(imageValidator.isValid(mockFile, context), "Expected false for invalid content type");
    }

    @Test
    @DisplayName("File with an invalid content type should return false")
    void isValid_FileWithInvalidContentType_ReturnsFalse() {
        when(mockFile.getContentType()).thenReturn("text/plain");

        assertFalse(imageValidator.isValid(mockFile, context), "Expected false for file with an invalid content type");
    }

    @Test
    @DisplayName("File with no content type should return false")
    void isValid_NoContentType_ReturnsFalse() {
        when(mockFile.getContentType()).thenReturn(null);

        assertFalse(imageValidator.isValid(mockFile, context), "Expected false for file with no content type");
    }


}

