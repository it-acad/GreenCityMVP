package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitAssignMapperTest {

    @InjectMocks
    private HabitAssignMapper mapper;

    @Test
    void convert() {
        // Arrange
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        UserShoppingListItem userShoppingListItemInProgress = ModelUtils.getUserShoppingListItem();
        UserShoppingListItem userShoppingListItemDone = ModelUtils.getUserShoppingListItem();

        userShoppingListItemInProgress.setStatus(ShoppingListItemStatus.INPROGRESS);
        userShoppingListItemDone.setStatus(ShoppingListItemStatus.DONE);

        List<UserShoppingListItem> userShoppingListItemsArrayList = new ArrayList<>();
        userShoppingListItemsArrayList.add(userShoppingListItemInProgress);
        userShoppingListItemsArrayList.add(userShoppingListItemDone);

        habitAssign.setUserShoppingListItems(userShoppingListItemsArrayList);

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
                .id(habitAssign.getId())
                .duration(habitAssign.getDuration())
                .habitStreak(habitAssign.getHabitStreak())
                .createDateTime(habitAssign.getCreateDate())
                .status(habitAssign.getStatus())
                .workingDays(habitAssign.getWorkingDays())
                .lastEnrollmentDate(habitAssign.getLastEnrollmentDate())
                .habit(HabitDto.builder()
                        .id(habitAssign.getHabit().getId())
                        .complexity(habitAssign.getHabit().getComplexity())
                        .defaultDuration(habitAssign.getHabit().getDefaultDuration())
                        .build())
                .userShoppingListItems(habitAssign.getUserShoppingListItems().stream()
                        .map(item -> UserShoppingListItemAdvanceDto.builder()
                                .id(item.getId())
                                .dateCompleted(item.getDateCompleted())
                                .status(item.getStatus())
                                .shoppingListItemId(item.getShoppingListItem().getId())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        HabitAssign expected = HabitAssign.builder()
                .id(habitAssignDto.getId())
                .duration(habitAssignDto.getDuration())
                .habitStreak(habitAssignDto.getHabitStreak())
                .createDate(habitAssignDto.getCreateDateTime())
                .status(habitAssignDto.getStatus())
                .workingDays(habitAssignDto.getWorkingDays())
                .lastEnrollmentDate(habitAssignDto.getLastEnrollmentDate())
                .habit(Habit.builder()
                        .id(habitAssignDto.getHabit().getId())
                        .complexity(habitAssignDto.getHabit().getComplexity())
                        .defaultDuration(habitAssign.getDuration())
                        .build())
                .userShoppingListItems(List.of(
                        UserShoppingListItem.builder()
                                .id(userShoppingListItemInProgress.getId())
                                .dateCompleted(userShoppingListItemInProgress.getDateCompleted())
                                .status(ShoppingListItemStatus.INPROGRESS)
                                .shoppingListItem(ShoppingListItem.builder()
                                        .id(userShoppingListItemInProgress.getShoppingListItem().getId())
                                        .build())
                                .build()))
                .build();

        // Act
        HabitAssign actual = mapper.convert(habitAssignDto);

        // Asser
        assertEquals(expected, actual);
    }
}
