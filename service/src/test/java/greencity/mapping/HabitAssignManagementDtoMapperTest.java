package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitAssignManagementDtoMapperTest {

    @InjectMocks
    private HabitAssignManagementDtoMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitAssignManagementDto expected = HabitAssignManagementDto.builder()
                .id(habitAssign.getId())
                .status(habitAssign.getStatus())
                .createDateTime(habitAssign.getCreateDate())
                .userId(habitAssign.getUser().getId())
                .habitId(habitAssign.getHabit().getId())
                .duration(habitAssign.getDuration())
                .habitStreak(habitAssign.getHabitStreak())
                .workingDays(habitAssign.getWorkingDays())
                .lastEnrollment(habitAssign.getLastEnrollmentDate())
                .build();

        // Act
        HabitAssignManagementDto actual = mapper.convert(habitAssign);

        // Assert
        assertEquals(expected, actual);
    }
}
