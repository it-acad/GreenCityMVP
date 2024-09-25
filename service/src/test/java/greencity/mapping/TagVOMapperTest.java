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
class TagVOMapperTest {

    @InjectMocks
    private TagVOMapper mapper;

    @Test
    void convert() {
        // Arrange
        Tag tag = ModelUtils.getTag();

        TagVO expected = ModelUtils.getTagVO();

        // Act
        TagVO actual = mapper.convert(tag);

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