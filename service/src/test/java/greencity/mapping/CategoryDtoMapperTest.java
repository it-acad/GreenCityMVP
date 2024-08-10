package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class CategoryDtoMapperTest {
    @InjectMocks
    private CategoryDtoMapper categoryDtoMapper;

    @Test
    void convertTest() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto("New Category");
        Category expected = Category.builder()
                .name(categoryDto.getName())
                .build();

        // Act
        Category actual = this.categoryDtoMapper.convert(categoryDto);

        // Assert
        assertEquals(expected, actual);
    }
}
