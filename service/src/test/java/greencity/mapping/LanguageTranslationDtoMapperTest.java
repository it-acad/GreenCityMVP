package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class LanguageTranslationDtoMapperTest {

    @InjectMocks
    private LanguageTranslationDtoMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitFactTranslation habitFactTranslation = ModelUtils.getFactTranslation();

        LanguageTranslationDTO expected = ModelUtils.getLanguageTranslationDTO();

        // Act
        LanguageTranslationDTO actual = mapper.convert(habitFactTranslation);

        String expectedLanguageCode = expected.getLanguage().getCode();
        String actualLanguageCode = actual.getLanguage().getCode();

        Long expectedLanguageId = expected.getLanguage().getId();
        Long actualLanguageId = actual.getLanguage().getId();

        // Asser
        assertEquals(expectedLanguageCode, actualLanguageCode);
        assertEquals(expectedLanguageId, actualLanguageId);
    }
}
