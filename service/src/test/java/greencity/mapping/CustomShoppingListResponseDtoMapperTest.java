package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class CustomShoppingListResponseDtoMapperTest {

    @InjectMocks
    private CustomShoppingListResponseDtoMapper mapper;

    @Test
    void convertTest() {
        //Arrange
        CustomShoppingListItem item = ModelUtils.getCustomShoppingListItem();

        CustomShoppingListItemResponseDto expected = CustomShoppingListItemResponseDto.builder()
                .id(item.getId())
                .text(item.getText())
                .status(item.getStatus())
                .build();

        // Act
        CustomShoppingListItemResponseDto actual = mapper.convert(item);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void mapToListTest() {
        // Arrange
        List<CustomShoppingListItem> itemList =
                Collections.singletonList(ModelUtils.getCustomShoppingListItem());

        // Act
        List<CustomShoppingListItemResponseDto> expected = itemList.stream()
                .map(customShoppingListItem -> CustomShoppingListItemResponseDto.builder()
                        .id(customShoppingListItem.getId())
                        .text(customShoppingListItem.getText())
                        .status(customShoppingListItem.getStatus())
                        .build())
                .toList();

        List<CustomShoppingListItemResponseDto> actual = mapper.mapAllToList(itemList);

        // Assert
        assertEquals(expected, actual);
    }
}
