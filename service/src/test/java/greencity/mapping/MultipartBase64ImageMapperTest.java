package greencity.mapping;

import greencity.exception.exceptions.NotSavedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class MultipartBase64ImageMapperTest {

    private final String VALID_BASE64_IMAGE_URL = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA\n" +
            "AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO\n" +
            "9TXL0Y4OHwAAAABJRU5ErkJggg==";

    private final String TYPE_OF_IMAGE = "image/jpeg";

    private final String INVALID_BASE64_IMAGE_URL = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA\\n\" +\n" +
            "                             \"AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO\\n\" +\n" +
            "                             \"9TXL0Y4OHwAAAABJRU5ErkJggg==";

    @InjectMocks
    MultipartBase64ImageMapper mapper;

    @Test
    void convert() throws IOException {
        // Act
        MultipartFile multipartFile = mapper.convert(VALID_BASE64_IMAGE_URL);

        String multipartFileContentType = multipartFile.getContentType();
        byte[] multipartFileByte = multipartFile.getBytes();

        // Assert
        assertNotNull(multipartFile);
        assertEquals(TYPE_OF_IMAGE, multipartFileContentType);
        assertNotNull(multipartFileByte);
    }

    @Test
    void convertWithIOException_ShouldThrowNotSavedException() {
        // Assert
        assertThrows(NotSavedException.class, () -> mapper.convert(INVALID_BASE64_IMAGE_URL));
    }
}
