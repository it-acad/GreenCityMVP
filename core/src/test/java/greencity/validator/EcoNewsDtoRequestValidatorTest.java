package greencity.validator;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getAddEcoNewsDtoRequest;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {

    @InjectMocks
    private EcoNewsDtoRequestValidator validator;
    private static final String validUrl = "https://google.com";
    private static final String invalidUrl = "invalidUrl";
    private static final List<String> validTags = Arrays.asList("tag1", "tag2");
    private static final List<String> tooManyValidTags = Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5");
    private static final AddEcoNewsDtoRequest request = getAddEcoNewsDtoRequest();


    @Test
    void isValid_withValidUrl_ExpectSuccess() {
        request.setSource(validUrl);
        request.setTags(validTags);
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValid_withInvalidUrl_shouldThrowException() {
        request.setSource(invalidUrl);
        request.setTags(validTags);

        assertThrows(InvalidURLException.class, () -> validator.isValid(request, null));
    }

    @Test
    void isValid_withTooManyTags_shouldThrowException() {
        request.setSource(validUrl);
        request.setTags(tooManyValidTags);

        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }

    @Test
    void isValid_withNoTags_shouldThrowException() {
        request.setSource(validUrl);
        request.setTags(Collections.emptyList());

        assertThrows(WrongCountOfTagsException.class, () -> validator.isValid(request, null));
    }
}
