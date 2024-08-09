package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.GetHabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

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
    private MockMvc mockMvc;
    private static final String habitStatisticControllerLink = "/habit/statistic";
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HabitStatisticService habitStatisticService;

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void findAllByHabitId_habitIdIsExists_ReturnStatusCode200() throws Exception {
        final long habitId = 1L;
        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenReturn(new GetHabitStatisticDto());

        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).findAllStatsByHabitId(habitId);
    }

    @Test
    public void findAllByHabitId_habitIdNotExists_ReturnStatusCode404() throws Exception {
        final long notExistsHabitId = 666L;
        when(habitStatisticService.findAllStatsByHabitId(666L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", notExistsHabitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(status().isNotFound());

        verify(habitStatisticService).findAllStatsByHabitId(notExistsHabitId);
    }

    @Test
    public void findAllStatsByHabitAssignId_HabitAssignIdExists_ReturnStatusCode200() throws Exception {
        final long habitAssignId = 1L;
        when(habitStatisticService.findAllStatsByHabitAssignId(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(habitStatisticControllerLink + "/assign/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    public void findAllStatsByHabitAssignId_HabitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        final long notExistsHabitAssignId = 666L;
        when(habitStatisticService.findAllStatsByHabitAssignId(notExistsHabitAssignId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(habitStatisticControllerLink + "/assign/{habitAssignId}", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(status().isNotFound());

        verify(habitStatisticService).findAllStatsByHabitAssignId(notExistsHabitAssignId);
    }

    @Test
    public void saveHabitStatistic_AddHabitStatisticDtoIsValid_ReturnStatusCode201() throws Exception {
        AddHabitStatisticDto addHabitStatisticDto = ModelUtils.addHabitStatisticDto();
        UserVO userVO = ModelUtils.getUserVO();
        String addHabitStatisticDtoAsJSON = objectMapper.writeValueAsString(addHabitStatisticDto);
        when(habitStatisticService.saveByHabitIdAndUserId(any(Long.class), any(Long.class), any(AddHabitStatisticDto.class))).thenReturn(new HabitStatisticDto());

        mockMvc.perform(post(habitStatisticControllerLink + "/{habitId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(addUserAsParams(userVO))
                        .content(addHabitStatisticDtoAsJSON))
                .andExpect(status().isCreated());

        verify(habitStatisticService).saveByHabitIdAndUserId(any(Long.class), any(Long.class), any(AddHabitStatisticDto.class));
    }

    @Test
    public void saveHabitStatistic_AddHabitStatisticDtoIsNotValid_ReturnStatusCode400() throws Exception {
        AddHabitStatisticDto alreadyExistsAddHabitStatisticDto = ModelUtils.addHabitStatisticDto();
        UserVO userVO = ModelUtils.getUserVO();
        String addHabitStatisticDtoAsJSON = objectMapper.writeValueAsString(alreadyExistsAddHabitStatisticDto);
        when(habitStatisticService.saveByHabitIdAndUserId(any(Long.class), any(Long.class), any(AddHabitStatisticDto.class))).thenThrow(NotSavedException.class);

        mockMvc.perform(post(habitStatisticControllerLink + "/{habitId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(addUserAsParams(userVO))
                        .content(addHabitStatisticDtoAsJSON))
                .andExpect(result -> assertInstanceOf(NotSavedException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        verify(habitStatisticService).saveByHabitIdAndUserId(any(Long.class), any(Long.class), any(AddHabitStatisticDto.class));
    }

    @Test
    public void updateStatistic_UpdateHabitStatisticDtoIsValid_ReturnStatusCode200() throws Exception {
        UpdateHabitStatisticDto updateHabitStatisticDto = ModelUtils.updateHabitStatisticDto();
        UserVO userVO = ModelUtils.getUserVO();
        String addHabitStatisticDtoAsJSON = objectMapper.writeValueAsString(updateHabitStatisticDto);
        when(habitStatisticService.update(any(Long.class), any(Long.class), any(UpdateHabitStatisticDto.class))).thenReturn(updateHabitStatisticDto);

        mockMvc.perform(put(habitStatisticControllerLink + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(addUserAsParams(userVO))
                        .content(addHabitStatisticDtoAsJSON))
                .andExpect(status().isOk());

        verify(habitStatisticService).update(any(Long.class), any(Long.class), any(UpdateHabitStatisticDto.class));
    }

    @Test
    public void updateStatistic_UpdateHabitStatisticDtoIsNotValid_ReturnStatusCode400() throws Exception {
        UpdateHabitStatisticDto notValidUpdateHabitStatisticDto = ModelUtils.updateHabitStatisticDto();
        notValidUpdateHabitStatisticDto.setHabitRate(null);
        UserVO userVO = ModelUtils.getUserVO();
        String addHabitStatisticDtoAsJSON = objectMapper.writeValueAsString(notValidUpdateHabitStatisticDto);

        mockMvc.perform(put(habitStatisticControllerLink + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(addUserAsParams(userVO))
                        .content(addHabitStatisticDtoAsJSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTodayStatisticsForAllHabitItems_withValidLocale_ReturnStatusCode200() throws Exception {
        Locale locale = Locale.ENGLISH;
        mockMvc.perform(get(habitStatisticControllerLink + "/todayStatisticsForAllHabitItems")
                        .contentType(MediaType.TEXT_PLAIN)
                        .locale(locale))
                .andExpect(status().isOk());

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(locale.getLanguage());
    }

    @Test
    public void getTodayStatisticsForAllHabitItems_withNotValidLocale_ReturnStatusCode400() throws Exception {
        Locale locale = Locale.of("NOT_EXISTS", "NOT_EXISTS");
        when(habitStatisticService.getTodayStatisticsForAllHabitItems(locale.getLanguage())).thenThrow(NotSavedException.class);
        mockMvc.perform(get(habitStatisticControllerLink + "/todayStatisticsForAllHabitItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(locale))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAmountOfAcquiredHabits_withExistsUserId_ReturnStatusCode200() throws Exception {
        final long existsUserId = 1L;
        mockMvc.perform(get(habitStatisticControllerLink + "/acquired/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(existsUserId)))
                .andExpect(status().isOk());

        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(existsUserId);
    }

    @Test
    public void findAmountOfHabitsInProgress_withExistsUserId_ReturnStatusCode200() throws Exception {
        final long existsUserId = 1L;

        mockMvc.perform(get(habitStatisticControllerLink + "/in-progress/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(existsUserId)))
                .andExpect(status().isOk());

        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(existsUserId);
    }

    private LinkedMultiValueMap<String, String> addUserAsParams(UserVO userVO) throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("id", userVO.getId().toString());
        requestParams.add("email", userVO.getEmail());
        requestParams.add("role", userVO.getRole().toString().toUpperCase(Locale.ROOT));

        return requestParams;
    }
}
