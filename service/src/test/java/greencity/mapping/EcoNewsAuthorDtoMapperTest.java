package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EcoNewsAuthorDtoMapperTest {

    @InjectMocks
    private EcoNewsAuthorDtoMapper mapper;

    @Test
    void convertTest() {
        //Arrange
        User author = ModelUtils.getUser();

        EcoNewsAuthorDto expected = EcoNewsAuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build();

        // Act
        EcoNewsAuthorDto actual = this.mapper.convert(author);

        // Assert
        assertEquals(expected, actual);
    }
}
