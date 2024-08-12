package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationDto;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class HabitFactDtoResponseMapperTest {
    @InjectMocks
    private HabitFactDtoResponseMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(1L)
                .translations(List.of(HabitFactTranslationVO.builder()
                        .factOfDayStatus(FactOfDayStatus.CURRENT)
                        .habitFact(HabitFactVO.builder()
                                .id(1L)
                                .habit(HabitVO.builder()
                                        .id(1L)
                                        .image("image")
                                        .complexity(1)
                                        .build())
                                .build())
                        .id(1L)
                        .language(ModelUtils.getLanguageVO())
                        .content("content")
                        .build()))
                .build();

        HabitFactDtoResponse expected = HabitFactDtoResponse.builder()
                .id(habitFactVO.getId())
                .habit(habitFactVO.getHabit())
                .translations(habitFactVO.getTranslations().stream().map(
                                habitFactTranslationVO -> HabitFactTranslationDto.builder()
                                        .id(habitFactTranslationVO.getId())
                                        .content(habitFactTranslationVO.getContent())
                                        .factOfDayStatus(habitFactTranslationVO.getFactOfDayStatus())
                                        .language(LanguageDTO.builder()
                                                .id(habitFactTranslationVO.getLanguage().getId())
                                                .code(habitFactTranslationVO.getLanguage().getCode())
                                                .build())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        // Act
        HabitFactDtoResponse actual = mapper.convert(habitFactVO);

        // Assert
        assertEquals(expected, actual);
    }
}
