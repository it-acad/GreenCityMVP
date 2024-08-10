package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CustomHabitMapperTest {

    @InjectMocks
    private CustomHabitMapper customHabitMapper;

    @Test
    public void convertTest() {
        // Arrange
        AddCustomHabitDtoRequest request = AddCustomHabitDtoRequest.builder()
                .image("test.png")
                .complexity(1)
                .defaultDuration(1)
                .build();

        Habit expected = Habit.builder()
                .image("test.png")
                .complexity(1)
                .defaultDuration(1)
                .isCustomHabit(true)
                .build();

        // Act
        Habit actual = customHabitMapper.convert(request);

        // Assert
        assertEquals(expected, actual);
    }
}
