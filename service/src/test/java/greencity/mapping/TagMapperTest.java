package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.enums.TagType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class TagMapperTest {

    @InjectMocks
    private TagMapper mapper;

    @Test
    void convert() {
        // Arrange
        TagVO tagVO = ModelUtils.getTagVO();

        Tag expected = ModelUtils.getTag();

        // Act
        Tag actual = mapper.convert(tagVO);

        Long expectedId = expected.getId();
        Long actualId = actual.getId();

        TagType expectedType = expected.getType();
        TagType actualType = actual.getType();

        Integer expectedSize = expected.getTagTranslations().size();
        Integer actualSize = actual.getTagTranslations().size();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(expectedType, actualType);
        assertEquals(expectedSize, actualSize);
    }
}
