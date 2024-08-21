package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitDtoMapperTest {

    @InjectMocks
    private HabitDtoMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("name")
                .description("description")
                .habitItem("item")
                .language(ModelUtils.getLanguage())
                .habit(Habit.builder()
                        .id(1L)
                        .image("image")
                        .complexity(1)
                        .defaultDuration(1)
                        .tags(new HashSet<>(ModelUtils.getTags()))
                        .shoppingListItems(Set.of(ShoppingListItem.builder()
                                .id(1L)
                                .translations(List.of(ShoppingListItemTranslation.builder()
                                        .content("content")
                                        .language(ModelUtils.getLanguage())
                                        .build())).build())).build())
                .build();

        Language language = habitTranslation.getLanguage();
        Habit habit = habitTranslation.getHabit();

        HabitDto expected = HabitDto.builder()
                .id(habit.getId())
                .image(habitTranslation.getHabit().getImage())
                .defaultDuration(habitTranslation.getHabit().getDefaultDuration())
                .complexity(habit.getComplexity())
                .habitTranslation(HabitTranslationDto.builder()
                        .description(habitTranslation.getDescription())
                        .habitItem(habitTranslation.getHabitItem())
                        .name(habitTranslation.getName())
                        .languageCode(language.getCode())
                        .build())
                .tags(habit.getTags().stream()
                        .flatMap(tag -> tag.getTagTranslations().stream())
                        .filter(tagTranslation -> tagTranslation.getLanguage().equals(language))
                        .map(TagTranslation::getName).collect(Collectors.toList()))
                .shoppingListItems(habit.getShoppingListItems() != null ? habit.getShoppingListItems().stream()
                        .map(shoppingListItem -> ShoppingListItemDto.builder()
                                .id(shoppingListItem.getId())
                                .status(ShoppingListItemStatus.ACTIVE.toString())
                                .text(shoppingListItem.getTranslations().stream()
                                        .filter(shoppingListItemTranslation -> shoppingListItemTranslation
                                                .getLanguage().equals(language))
                                        .map(ShoppingListItemTranslation::getContent)
                                        .findFirst().orElse(null))
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();

        // Act
        HabitDto actual = mapper.convert(habitTranslation);

        // Assert
        assertEquals(expected, actual);
    }
}
