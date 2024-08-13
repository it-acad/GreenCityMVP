package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.GetHabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitStatisticControllerTest {
    private final long habitId = 1L;
    private final long notValidHabitId = 999L;
    final long existsUserId = 1L;
    private MockMvc mockMvc;
    private static final String habitStatisticControllerLink = "/habit/statistic";
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserVO userVO = ModelUtils.getUserVO();
    private final Principal principal = ModelUtils.getPrincipal();

    @Mock
    private HabitStatisticService habitStatisticService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void findAllByHabitId_HabitIdIsExists_ReturnStatusCode200() throws Exception {
        final long habitId = 1L;
        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenReturn(new GetHabitStatisticDto());

        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).findAllStatsByHabitId(habitId);
    }

    @Test
    public void findAllByHabitId_HabitIdNotExists_ReturnStatusCode404() throws Exception {
        final long notExistsHabitId = 666L;
        when(habitStatisticService.findAllStatsByHabitId(666L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", notExistsHabitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(status().isNotFound());

        verify(habitStatisticService, times(1)).findAllStatsByHabitId(notExistsHabitId);
    }

    @Test
    public void findAllStatsByHabitAssignId_HabitAssignIdExists_ReturnStatusCode200() throws Exception {
        final long habitAssignId = 1L;
        when(habitStatisticService.findAllStatsByHabitAssignId(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(habitStatisticControllerLink + "/assign/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    public void findAllStatsByHabitAssignId_HabitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        final long notExistsHabitAssignId = 666L;
        when(habitStatisticService.findAllStatsByHabitAssignId(notExistsHabitAssignId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(habitStatisticControllerLink + "/assign/{habitAssignId}", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(status().isNotFound());

        verify(habitStatisticService, times(1)).findAllStatsByHabitAssignId(notExistsHabitAssignId);
    }

    @Test
    public void saveHabitStatistic_AddHabitStatisticDtoIsValid_ReturnStatusCode201() throws Exception {
        final AddHabitStatisticDto addHabitStatisticDto = ModelUtils.addHabitStatisticDto();
        when(habitStatisticService.saveByHabitIdAndUserId(anyLong(), anyLong(), any(AddHabitStatisticDto.class))).thenReturn(new HabitStatisticDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitStatisticControllerLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(addHabitStatisticDto)))
                .andExpect(status().isCreated());

        verify(habitStatisticService, times(1)).saveByHabitIdAndUserId(anyLong(), anyLong(), any(AddHabitStatisticDto.class));
    }

    @Test
    public void saveHabitStatistic_AddHabitStatisticDtoIsNotValid_ReturnStatusCode400() throws Exception {
        AddHabitStatisticDto alreadyExistsAddHabitStatisticDto = ModelUtils.addHabitStatisticDto();
        when(habitStatisticService.saveByHabitIdAndUserId(any(Long.class), any(Long.class), any(AddHabitStatisticDto.class))).thenThrow(NotSavedException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitStatisticControllerLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(alreadyExistsAddHabitStatisticDto)))
                .andExpect(result -> assertInstanceOf(NotSavedException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        verify(habitStatisticService, times(1)).saveByHabitIdAndUserId(any(Long.class), any(Long.class), any(AddHabitStatisticDto.class));
    }

    @Test
    public void updateStatistic_UpdateHabitStatisticDtoIsValid_ReturnStatusCode200() throws Exception {
        UpdateHabitStatisticDto updateHabitStatisticDto = ModelUtils.updateHabitStatisticDto();
        when(habitStatisticService.update(any(Long.class), any(Long.class), any(UpdateHabitStatisticDto.class))).thenReturn(updateHabitStatisticDto);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitStatisticControllerLink + "/{id}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).update(any(Long.class), any(Long.class), any(UpdateHabitStatisticDto.class));
    }

    @Test
    public void updateStatistic_UpdateHabitStatisticDtoIsNotValid_ReturnStatusCode400() throws Exception {
        UpdateHabitStatisticDto notValidUpdateHabitStatisticDto = ModelUtils.updateHabitStatisticDto();
        notValidUpdateHabitStatisticDto.setHabitRate(null);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitStatisticControllerLink + "/{id}", notValidHabitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(notValidUpdateHabitStatisticDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTodayStatisticsForAllHabitItems_WithValidLocale_ReturnStatusCode200() throws Exception {
        Locale locale = Locale.ENGLISH;

        mockMvc.perform(get(habitStatisticControllerLink + "/todayStatisticsForAllHabitItems")
                        .contentType(MediaType.TEXT_PLAIN)
                        .locale(locale))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).getTodayStatisticsForAllHabitItems(locale.getLanguage());
    }

    @Test
    public void getTodayStatisticsForAllHabitItems_WithNotValidLocale_ReturnStatusCode400() throws Exception {
        Locale locale = Locale.of("NOT_EXISTS", "NOT_EXISTS");
        when(habitStatisticService.getTodayStatisticsForAllHabitItems(locale.getLanguage())).thenThrow(NotSavedException.class);

        mockMvc.perform(get(habitStatisticControllerLink + "/todayStatisticsForAllHabitItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(locale))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAmountOfAcquiredHabits_WithExistsUserId_ReturnStatusCode200() throws Exception {
        mockMvc.perform(get(habitStatisticControllerLink + "/acquired/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(existsUserId)))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).getAmountOfAcquiredHabitsByUserId(existsUserId);
    }

    @Test
    public void findAmountOfHabitsInProgress_WithExistsUserId_ReturnStatusCode200() throws Exception {
        mockMvc.perform(get(habitStatisticControllerLink + "/in-progress/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(existsUserId)))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).getAmountOfHabitsInProgressByUserId(existsUserId);
    }
}
