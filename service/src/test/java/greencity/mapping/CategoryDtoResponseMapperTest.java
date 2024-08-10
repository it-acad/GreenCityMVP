package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CategoryDtoResponseMapperTest {

    @InjectMocks
    private CategoryDtoResponseMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        Category category = Category.builder()
                .id(1L)
                .name("New Category")
                .build();

        CategoryDtoResponse expected = CategoryDtoResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        // Act
        CategoryDtoResponse actual = mapper.convert(category);

        // Assert
        assertEquals(expected, actual);
    }
}
