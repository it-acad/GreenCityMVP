package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class ShoppingListItemDtoMapperTest {

    @InjectMocks
    private ShoppingListItemDtoMapper mapper;

    @Test
    void convert() {
        // Arrange
        ShoppingListItemTranslation shoppingListItemTranslation = ModelUtils.getShoppingListItemTranslations().getFirst();

        ShoppingListItemDto expected = ShoppingListItemDto.builder()
                .id(shoppingListItemTranslation.getShoppingListItem().getId())
                .text(shoppingListItemTranslation.getContent())
                .status(ShoppingListItemStatus.ACTIVE.name())
                .build();

        // Act
        ShoppingListItemDto actual = mapper.convert(shoppingListItemTranslation);

        Long expectedId = expected.getId();
        Long actualId = actual.getId();

        String expectedText = expected.getText();
        String actualText = actual.getText();

        String expectedStatus = expected.getStatus();
        String actualStatus = actual.getStatus();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(expectedStatus, actualStatus);
        assertEquals(expectedText, actualText);
    }
}
