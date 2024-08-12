package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ShoppingListItemWithStatusRequestDtoMapperTest {

    @InjectMocks
    private ShoppingListItemWithStatusRequestDtoMapper mapper;

    @Test
    void convert() {
        // Arrange
        ShoppingListItemWithStatusRequestDto requestDto = ShoppingListItemWithStatusRequestDto.builder()
                .id(1L)
                .status(ShoppingListItemStatus.DONE)
                .build();

        UserShoppingListItem expected = ModelUtils.getUserShoppingListItem();

        // Act
        UserShoppingListItem actual = mapper.convert(requestDto);

        ShoppingListItemStatus expectedStatus = expected.getStatus();
        ShoppingListItemStatus actualStatus = actual.getStatus();

        ShoppingListItem expectedShoppingListItem = expected.getShoppingListItem();
        ShoppingListItem actualShoppingListItem = actual.getShoppingListItem();

        // Assert
        assertEquals(expectedStatus, actualStatus);
        assertEquals(expectedShoppingListItem, actualShoppingListItem);
    }
}