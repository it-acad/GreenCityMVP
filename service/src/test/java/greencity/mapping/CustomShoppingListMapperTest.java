package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CustomShoppingListMapperTest {

    @InjectMocks
    private CustomShoppingListMapper customShoppingListMapper;

    @Test
    public void convertTest() {
        CustomShoppingListItemResponseDto dto = ModelUtils.getCustomShoppingListItemResponseDto();

        CustomShoppingListItem expected = CustomShoppingListItem.builder()
                .id(dto.getId())
                .text(dto.getText())
                .status(dto.getStatus())
                .build();

        assertEquals(expected, customShoppingListMapper.convert(dto));
    }

    @Test
    public void mapToAllListTest() {
        CustomShoppingListItemResponseDto dto = ModelUtils.getCustomShoppingListItemResponseDto();
        List<CustomShoppingListItemResponseDto> dtoList = Collections.singletonList(dto);

        List<CustomShoppingListItem> expected = Collections.singletonList(CustomShoppingListItem.builder()
                .id(dto.getId())
                .text(dto.getText())
                .status(dto.getStatus())
                .build());

        assertEquals(expected, customShoppingListMapper.mapAllToList(dtoList));
    }
}
