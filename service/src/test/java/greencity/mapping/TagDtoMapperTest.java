package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class TagDtoMapperTest {

    @InjectMocks
    private TagDtoMapper mapper;

    @Test
    void convert() {
        // Arrange
        Tag tag = ModelUtils.getTag();

        TagTranslation tagTranslation = TagTranslation.builder()
                .id(1L)
                .name("News")
                .language(ModelUtils.getLanguage())
                .tag(tag)
                .build();

        TagDto expected = TagDto.builder()
                .id(tagTranslation.getTag().getId())
                .name(tagTranslation.getName())
                .build();

        // Act
        TagDto actual = mapper.convert(tagTranslation);

        Long expectedId = expected.getId();
        Long actualId = actual.getId();

        String expectedName = expected.getName();
        String actualType = actual.getName();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(expectedName, actualType);
    }
}
