package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitAssignService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class HabitAssignControllerTest {
    final long habitId = 1L;
    final long notExistsHabitId = 666L;
    final long habitAssignId = 1L;
    final long notExistsHabitAssignId = 666L;
    private MockMvc mockMvc;
    private static final String habitAssignControllerLink = "/habit/assign";
    private final UserVO userVO = ModelUtils.getUserVO();
    private final Principal principal = ModelUtils.getPrincipal();
    private final Locale localeEn = Locale.ENGLISH;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();


    @InjectMocks
    private HabitAssignController habitAssignController;

    @Mock
    private HabitAssignService habitAssignService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void assignDefault_habitIdExists_ReturnStatusCode201() throws Exception {
        when(habitAssignService.assignDefaultHabitForUser(habitId, userVO)).thenReturn(new HabitAssignManagementDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(habitAssignService, times(1)).assignDefaultHabitForUser(habitId, userVO);
    }

    @Test
    public void assignDefault_habitIdNotExists_ReturnStatusCode404() throws Exception {
        when(habitAssignService.assignDefaultHabitForUser(notExistsHabitId, userVO)).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}", notExistsHabitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).assignDefaultHabitForUser(notExistsHabitId, userVO);
    }

    @Test
    public void assignCustom_habitIdExists_ReturnStatusCode201() throws Exception {
        final HabitAssignCustomPropertiesDto customHabit = ModelUtils.getHabitAssignCustomPropertiesDto();
        when(habitAssignService.assignCustomHabitForUser(habitId, userVO, customHabit)).thenReturn(new ArrayList<>());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}/custom", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(customHabit)))
                .andExpect(status().isCreated());

        verify(habitAssignService, times(1)).assignCustomHabitForUser(habitId, userVO, customHabit);
    }

    @Test
    public void assignCustom_habitIdNotExists_ReturnStatusCode404() throws Exception {
        final HabitAssignCustomPropertiesDto customHabit = ModelUtils.getHabitAssignCustomPropertiesDto();
        when(habitAssignService.assignCustomHabitForUser(notExistsHabitId, userVO, customHabit)).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitId}/custom", notExistsHabitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(customHabit)))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).assignCustomHabitForUser(notExistsHabitId, userVO, customHabit);
    }

    @Test
    public void updateHabitAssignDuration_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        final int duration = AppConstant.MIN_DAYS_DURATION;
        when(habitAssignService.updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration)).thenReturn(new HabitAssignUserDurationDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/update-habit-duration", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("duration", String.valueOf(duration)))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration);
    }

    @Test
    public void updateHabitAssignDuration_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        final int duration = AppConstant.MIN_DAYS_DURATION;
        when(habitAssignService.updateUserHabitInfoDuration(notExistsHabitAssignId, userVO.getId(), duration)).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/update-habit-duration", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("duration", String.valueOf(duration)))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(notExistsHabitAssignId, userVO.getId(), duration);
    }

    @Test
    public void updateHabitAssignDuration_durationOutOfRange_ReturnStatusCode400() throws Exception {
        final int wrongDuration = AppConstant.MIN_DAYS_DURATION - 1;
        when(habitAssignService.updateUserHabitInfoDuration(habitAssignId, userVO.getId(), wrongDuration)).thenThrow(BadRequestException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/update-habit-duration", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .param("duration", String.valueOf(wrongDuration)))
                .andExpect(status().isBadRequest());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(habitAssignId, userVO.getId(), wrongDuration);
    }

    @Test
    public void getHabitAssign_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        when(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), localeEn.getLanguage())).thenReturn(new HabitAssignDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), localeEn.getLanguage());
    }

    @Test
    public void getHabitAssign_habitAssignIdNotExists_ReturnStatusCode400() throws Exception {
        when(habitAssignService.getByHabitAssignIdAndUserId(notExistsHabitAssignId, userVO.getId(), localeEn.getLanguage())).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).getByHabitAssignIdAndUserId(notExistsHabitAssignId, userVO.getId(), localeEn.getLanguage());
    }

    @Test
    public void getHabitAssign_notValidLocale_ReturnStatusCode400() throws Exception {
        final Locale notValidLocale = Locale.of("NOT_VALID_LANG", "NOT_VALID_COUNTRY");
        when(habitAssignService.getByHabitAssignIdAndUserId(notExistsHabitAssignId, userVO.getId(), notValidLocale.getLanguage())).thenThrow(BadRequestException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(notValidLocale))
                .andExpect(status().isBadRequest());

        verify(habitAssignService, times(1)).getByHabitAssignIdAndUserId(notExistsHabitAssignId, userVO.getId(), notValidLocale.getLanguage());
    }

    @Test
    public void getCurrentUserHabitAssignsByIdAndAcquired_withValidUserAndValidLocale_ReturnStatusCode200() throws Exception {
        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), localeEn.getLanguage())).thenReturn(new ArrayList<>());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/allForCurrentUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), localeEn.getLanguage());
    }

    @Test
    public void getUserShoppingAndCustomShoppingLists_habitAssignIdValid_ReturnStatusCode200() throws Exception {
        when(habitAssignService.getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, localeEn.getLanguage())).thenReturn(new UserShoppingAndCustomShoppingListsDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, localeEn.getLanguage());
    }

    @Test
    public void getUserShoppingAndCustomShoppingLists_habitAssignIdNotValid_ReturnStatusCode404() throws Exception {
        when(habitAssignService.getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, localeEn.getLanguage())).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, localeEn.getLanguage());
    }

    @Test
    public void updateUserAndCustomShoppingLists_listsDtoNotNull_ReturnStatusCode200() throws Exception {
        final UserShoppingAndCustomShoppingListsDto listsDto = ModelUtils.getUserShoppingAndCustomShoppingListsDto();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn)
                        .content(objectMapper.writeValueAsString(listsDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(userVO.getId(), habitAssignId, listsDto, localeEn.getLanguage());
    }

    @Test
    public void getListOfUserAndCustomShoppingListsInprogress_withValidUserAndValidLocale_ReturnStatusCode200() throws Exception {
        when(habitAssignService.getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), localeEn.getLanguage())).thenReturn(new ArrayList<>());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/allUserAndCustomShoppingListsInprogress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), localeEn.getLanguage());
    }

    @Test
    public void getAllHabitAssignsByHabitIdAndAcquired_habitIdValid_ReturnStatusCode200() throws Exception {
        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, localeEn.getLanguage())).thenReturn(new ArrayList<>());

        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/all", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, localeEn.getLanguage());
    }

    @Test
    public void getHabitAssignByHabitId_habitIdExists_ReturnStatusCode200() throws Exception {
        when(habitAssignService.findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, localeEn.getLanguage())).thenReturn(new HabitAssignDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/active", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, localeEn.getLanguage());
    }

    @Test
    public void getHabitAssignByHabitId_habitIdNotExists_ReturnStatusCode404() throws Exception {
        when(habitAssignService.findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, localeEn.getLanguage())).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitId}/active", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, localeEn.getLanguage());
    }

    @Test
    public void getUsersHabitByHabitAssignId_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        when(habitAssignService.findHabitByUserIdAndHabitAssignId(userVO.getId(), habitAssignId, localeEn.getLanguage())).thenReturn(new HabitDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/more", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findHabitByUserIdAndHabitAssignId(userVO.getId(), habitAssignId, localeEn.getLanguage());
    }

    @Test
    public void getUsersHabitByHabitAssignId_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        when(habitAssignService.findHabitByUserIdAndHabitAssignId(userVO.getId(), notExistsHabitAssignId, localeEn.getLanguage())).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/{habitAssignId}/more", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).findHabitByUserIdAndHabitAssignId(userVO.getId(), notExistsHabitAssignId, localeEn.getLanguage());
    }

    @Test
    public void updateAssignByHabitId_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        final HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.INPROGRESS);
        when(habitAssignService.updateStatusByHabitAssignId(habitAssignId, habitAssignStatDto)).thenReturn(new HabitAssignManagementDto());

        mockMvc.perform(patch(habitAssignControllerLink + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(localeEn)
                        .content(objectMapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateStatusByHabitAssignId(habitAssignId, habitAssignStatDto);
    }

    @Test
    public void updateAssignByHabitId_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        final HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.INPROGRESS);
        when(habitAssignService.updateStatusByHabitAssignId(notExistsHabitAssignId, habitAssignStatDto)).thenThrow(NotFoundException.class);

        mockMvc.perform(patch(habitAssignControllerLink + "/{habitAssignId}", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(localeEn)
                        .content(objectMapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).updateStatusByHabitAssignId(notExistsHabitAssignId, habitAssignStatDto);
    }

    @Test
    public void enrollHabit_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        final LocalDate date = LocalDate.now();
        when(habitAssignService.enrollHabit(habitAssignId, userVO.getId(), date, localeEn.getLanguage())).thenReturn(new HabitAssignDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/enroll/{date}", habitAssignId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).enrollHabit(habitAssignId, userVO.getId(), date, localeEn.getLanguage());
    }

    @Test
    public void enrollHabit_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        final LocalDate date = LocalDate.now();
        when(habitAssignService.enrollHabit(notExistsHabitAssignId, userVO.getId(), date, localeEn.getLanguage())).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/enroll/{date}", notExistsHabitAssignId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).enrollHabit(notExistsHabitAssignId, userVO.getId(), date, localeEn.getLanguage());
    }

    @Test
    public void unenrollHabit_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        final LocalDate date = LocalDate.now();
        when(habitAssignService.unenrollHabit(habitAssignId, userVO.getId(), date)).thenReturn(new HabitAssignDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/unenroll/{date}", habitAssignId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).unenrollHabit(habitAssignId, userVO.getId(), date);
    }

    @Test
    public void unenrollHabit_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        final LocalDate date = LocalDate.now();
        when(habitAssignService.unenrollHabit(notExistsHabitAssignId, userVO.getId(), date)).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(habitAssignControllerLink + "/{habitAssignId}/unenroll/{date}", notExistsHabitAssignId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).unenrollHabit(notExistsHabitAssignId, userVO.getId(), date);
    }

    @Test
    public void getInprogressHabitAssignOnDate_withAllValidParams_ReturnStatusCode200() throws Exception {
        final LocalDate date = LocalDate.now();
        when(habitAssignService.findInprogressHabitAssignsOnDate(userVO.getId(), date, localeEn.getLanguage())).thenReturn(new ArrayList<>());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/active/{date}", date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findInprogressHabitAssignsOnDate(userVO.getId(), date, localeEn.getLanguage());
    }

    @Test
    public void getHabitAssignBetweenDates_withAllValidParams_ReturnStatusCode200() throws Exception {
        final LocalDate from = LocalDate.now().minusDays(10);
        final LocalDate to = LocalDate.now();
        when(habitAssignService.findHabitAssignsBetweenDates(userVO.getId(), from, to, localeEn.getLanguage())).thenReturn(new ArrayList<>());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignControllerLink + "/activity/{from}/to/{to}", from, to)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).findHabitAssignsBetweenDates(userVO.getId(), from, to, localeEn.getLanguage());
    }

    @Test
    public void cancelHabitAssign_habitIdExists_ReturnStatusCode200() throws Exception {
        when(habitAssignService.cancelHabitAssign(habitId, userVO.getId())).thenReturn(new HabitAssignDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);


        mockMvc.perform(patch(habitAssignControllerLink + "/cancel/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).cancelHabitAssign(habitId, userVO.getId());
    }

    @Test
    public void cancelHabitAssign_habitIdNotExists_ReturnStatusCode404() throws Exception {
        when(habitAssignService.cancelHabitAssign(notExistsHabitId, userVO.getId())).thenThrow(NotFoundException.class);
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(habitAssignControllerLink + "/cancel/{habitId}", notExistsHabitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .locale(localeEn))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).cancelHabitAssign(notExistsHabitId, userVO.getId());
    }

    @Test
    public void deleteHabitAssign_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(habitAssignControllerLink + "/delete/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).deleteHabitAssign(habitAssignId, userVO.getId());
    }

    @Test
    public void deleteHabitAssign_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        doThrow(NotFoundException.class).when(habitAssignService).deleteHabitAssign(notExistsHabitAssignId, userVO.getId());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(habitAssignControllerLink + "/delete/{habitAssignId}", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).deleteHabitAssign(notExistsHabitAssignId, userVO.getId());
    }

    @Test
    public void updateShoppingListStatus_withValidUpdateUserShoppingListDto_ReturnStatusCode200() throws Exception {
        final UpdateUserShoppingListDto updateUserShoppingListDto = ModelUtils.getUpdateUserShoppingListDto();

        mockMvc.perform(put(habitAssignControllerLink + "/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(updateUserShoppingListDto)))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateUserShoppingListItem(updateUserShoppingListDto);
    }

    @Test
    public void updateShoppingListStatus_withNotValidUpdateUserShoppingListDto_ReturnStatusCode404() throws Exception {
        final UpdateUserShoppingListDto updateUserShoppingListDto = ModelUtils.getUpdateUserShoppingListDto();
        updateUserShoppingListDto.setHabitAssignId(notExistsHabitAssignId);
        doThrow(NotFoundException.class).when(habitAssignService).updateUserShoppingListItem(updateUserShoppingListDto);

        mockMvc.perform(put(habitAssignControllerLink + "/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(updateUserShoppingListDto)))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).updateUserShoppingListItem(updateUserShoppingListDto);
    }

    @Test
    public void updateProgressNotificationHasDisplayed_habitAssignIdExists_ReturnStatusCode200() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateProgressNotificationHasDisplayed(habitAssignId, userVO.getId());
    }

    @Test
    public void updateProgressNotificationHasDisplayed_habitAssignIdNotExists_ReturnStatusCode404() throws Exception {
        doThrow(NotFoundException.class).when(habitAssignService).updateProgressNotificationHasDisplayed(notExistsHabitAssignId, userVO.getId());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(put(habitAssignControllerLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", notExistsHabitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).updateProgressNotificationHasDisplayed(notExistsHabitAssignId, userVO.getId());
    }
}
