package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {

    @Mock
    private LanguageService languageService;
    @InjectMocks
    private LanguageValidator languageValidator;

    @BeforeEach
    void setUp() {
        List<String> languageCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(languageCodes);

        languageValidator.initialize(null);
    }

    @Test
    void englishLanguage_ExistsInAList_ExpectSuccess() {
        Locale locale = Locale.ENGLISH;
        assertTrue(languageValidator.isValid(locale, null));
    }

    @Test
    void japaneseLanguage_NotExistsInAList_ExpectFail() {
        Locale locale = Locale.JAPANESE;
        assertFalse(languageValidator.isValid(locale, null));
    }

    @Test
    void invalidLanguage_NullValue_ExpectException() {
        assertThrows(NullPointerException.class, () -> languageValidator.isValid(null, null));
    }
}
