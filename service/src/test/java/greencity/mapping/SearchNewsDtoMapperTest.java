package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class SearchNewsDtoMapperTest {

    @InjectMocks
    private SearchNewsDtoMapper mapper;

    @Test
    void convert() {
        // Arrange
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        EcoNews ecoNews = ModelUtils.getEcoNews();
        SearchNewsDto expected = ModelUtils.getSearchNewsDto();

        // Act
        SearchNewsDto actual = mapper.convert(ecoNews);

        Long expectedId = expected.getId();
        Long actualId = actual.getId();

        String expectedTitle = expected.getTitle();
        String actualTitle = actual.getTitle();

        String expectedAuthorName = expected.getAuthor().getName();
        String actualAuthorName = actual.getAuthor().getName();

        Integer expectedTagsSize = expected.getTags().size();
        Integer actualTagsSize = actual.getTags().size();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedAuthorName, actualAuthorName);
        assertEquals(expectedTagsSize, actualTagsSize);
    }
}
