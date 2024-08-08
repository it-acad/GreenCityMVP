package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitService;
import greencity.service.TagsService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitControllerTest {

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitService habitService;

    @Mock
    private UserService userService;

    @Mock
    private TagsService tagsService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Principal principal;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.habitController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(this.userService, this.modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(new DefaultErrorAttributes(),
                        this.objectMapper))
                .build();
    }

    @Test
    void getHabitById_WithValidId_StatusOk() throws Exception {
        //given
        Long habitId = 1L;

        //when
        this.mockMvc.perform(get("/habit/{id}", habitId)
                        .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        verify(this.habitService).getByIdAndLanguageCode(habitId, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getAllHabits_WithValidPageAndSize_StatusOk() throws Exception {
        //given
        UserVO userVO = ModelUtils.getUserVO();
        String userEmail = userVO.getEmail();
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        //when
        when(this.userService.findByEmail(anyString())).thenReturn(userVO);
        this.mockMvc.perform(get("/habit")
                .param("page", String.valueOf(pageNumber))
                .param("size", String.valueOf(pageSize))
                .locale(Locale.ENGLISH)
                .principal(() -> userEmail)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        //when
        verify(this.userService).findByEmail(userEmail);
        verify(this.habitService).getAllHabitsByLanguageCode(userVO, pageable, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getShoppingListItems_ByHabitId_StatusOk() throws Exception {
        //given
        Long habitId = 1L;

        //when
        this.mockMvc.perform(get("/habit/{id}/shopping-list", habitId)
                .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    String contentType = result.getResponse().getContentType();
                    System.out.println("Response Content-Type: " + contentType);
                    System.out.println("Response Body: " + responseBody);
                })
                .andDo(print());

        //then
        verify(this.habitService).getShoppingListForHabit(habitId, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getAllHabitsByTagsAndLanguage_WithValidParameters_StatusOk() throws Exception {
        //given
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tagList = Collections.singletonList("eco");

        //when
        this.mockMvc.perform(get("/habit/tags/search")
                .param("page", String.valueOf(pageNumber))
                .param("size", String.valueOf(pageSize))
                        .param("tags", tagList.toArray(String[]::new))
                        .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentType = result.getResponse().getContentType();
                    if (contentType != null) {
                        assertTrue((BooleanSupplier) content().contentType(MediaType.APPLICATION_JSON_VALUE), "Expected content type 'application/json'");
                    }
                })
                .andDo(print());

        //then
        verify(this.habitService).getAllByTagsAndLanguageCode(eq(pageable), eq(tagList), eq(Locale.ENGLISH.getLanguage()));
    }

    @Test
    void getAllHabitsByDifferentParameters_WithValidInput_StatusOk() throws Exception {
        //given
        UserVO userVO = ModelUtils.getUserVO();
        String userEmail = userVO.getEmail();
        boolean isCustomHabit = true;
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tags = Collections.singletonList("eco");
        List<Integer> complexities = Collections.singletonList(1);

        //when
        when(this.userService.findByEmail(anyString())).thenReturn(userVO);
        this.mockMvc.perform(get("/habit/search")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                        .param("tags", tags.toArray(String[]::new))
                        .param("isCustomHabit", String.valueOf(isCustomHabit))
                        .param("complexities", complexities.stream().map(String::valueOf).toArray(String[]::new))
                        .principal(() -> userEmail)
                        .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        verify(this.userService).findByEmail(userEmail);
        verify(this.habitService).getAllByDifferentParameters(userVO, pageable, Optional.of(tags),
                Optional.of(isCustomHabit), Optional.of(complexities), Locale.ENGLISH.getLanguage());
    }

    @Test
    void getAllHabitsByDifferentParameters_WithInvalidInput_StatusBadRequest() throws Exception {
        //given
        UserVO userVO = ModelUtils.getUserVO();
        String userEmail = userVO.getEmail();
        when(this.userService.findByEmail(anyString())).thenReturn(userVO);

        //when
        this.mockMvc.perform(get("/habit/search")
                        .principal(() -> userEmail)
                        .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andDo(print());

        //then
        verify(this.userService).findByEmail(userEmail);
    }

    @Test
    void findAllHabitsTags_WithLocale_StatusOk() throws Exception {
        //when
        this.mockMvc.perform(get("/habit/tags")
                .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        verify(this.tagsService).findAllHabitsTags(Locale.ENGLISH.getLanguage());
    }

    @Test
    void addCustomHabit_WithValidRequest_StatusCreated() throws Exception {
        //given
        AddCustomHabitDtoRequest dtoRequest = ModelUtils.getAddCustomHabitDtoRequest();
        String jsonRequest = new ObjectMapper().writeValueAsString(dtoRequest);
        MockMultipartFile requestFile =
                new MockMultipartFile("request", "request.json", String.valueOf(MediaType.APPLICATION_JSON), jsonRequest.getBytes());

        //when
        when(this.principal.getName()).thenReturn("Dmytro@gmail.com");

        this.mockMvc.perform(multipart("/habit/custom")
                        .file(requestFile)
                        .principal(this.principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andDo(print());

        //then
        verify(this.habitService).addCustomHabit(eq(dtoRequest), isNull(), eq("Dmytro@gmail.com"));
    }

    @Test
    void getFriendsAssignedToHabitProfilePictures_WithValidHabitId_StatusOk() throws Exception {
        //given
        Long habitId = 1L;
        UserVO userVO = ModelUtils.getUserVO();
        String userEmail = userVO.getEmail();
        Long userId = userVO.getId();

        //when
        when(this.userService.findByEmail(anyString())).thenReturn(userVO);

        this.mockMvc.perform(get("/habit/{habitId}/friends/profile-pictures", habitId)
                        .principal(() -> userEmail)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        verify(this.userService).findByEmail(userEmail);
        verify(this.habitService).getFriendsAssignedToHabitProfilePictures(habitId, userId);
    }
}
