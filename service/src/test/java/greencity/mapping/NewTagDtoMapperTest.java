package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class NewTagDtoMapperTest {

    @InjectMocks
    private NewTagDtoMapper mapper;

    @Test
    public void convertTest() {
            // Arrange
            Tag tag = ModelUtils.getTag();
            NewTagDto expected = NewTagDto.builder()
                    .id(tag.getId())
                    .name(tag.getTagTranslations().get(1).getName())
                    .nameUa(tag.getTagTranslations().get(0).getName())
                    .build();

            // Act
            NewTagDto actual = mapper.convert(tag);

            Long expectedId = expected.getId();
            Long actualId = actual.getId();

            String expectedName = expected.getName();
            String actualName = actual.getName();

            String expectedNameUa = expected.getNameUa();
            String actualNameUa = actual.getNameUa();

            // Asser
            assertEquals(expectedId, actualId);
            assertEquals(expectedName, actualName);
            assertEquals(expectedNameUa, actualNameUa);
    }
}
