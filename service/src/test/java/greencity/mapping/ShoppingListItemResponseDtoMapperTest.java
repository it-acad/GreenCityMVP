package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ShoppingListItemResponseDtoMapperTest {
    @InjectMocks
    private ShoppingListItemResponseDtoMapper mapper;

    @Test
    void convert() {
        // Arrange
        ShoppingListItem shoppingListItem = ModelUtils.getShoppingListItem();
        List<ShoppingListItemTranslation> shoppingListItemTranslations = ModelUtils.getShoppingListItemTranslations();

        List<ShoppingListItemTranslationDTO> shoppingListItemTranslationDTOS = shoppingListItemTranslations.stream()
                .map(translation -> ShoppingListItemTranslationDTO.builder()
                        .id(translation.getId())
                        .content(translation.getContent())
                        .build())
                .toList();

        ShoppingListItemResponseDto expected = ShoppingListItemResponseDto.builder()
                .id(1L)
                .translations(shoppingListItemTranslationDTOS)
                .build();

        // Act
        ShoppingListItemResponseDto actual = mapper.convert(shoppingListItem);

        Long expectedId = expected.getId();
        Long actualId = actual.getId();

        List<ShoppingListItemTranslationDTO> expectedTranslation = expected.getTranslations();
        List<ShoppingListItemTranslationDTO> actualTranslation = actual.getTranslations();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(expectedTranslation, actualTranslation);
    }
}