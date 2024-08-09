package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habitfact.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {

    private MockMvc mockMvc;
    @Mock
    private HabitFactService habitFactService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ModelMapper mapper;
    @Mock
    private Validator mockValidator;
    @InjectMocks
    private HabitFactController habitFactController;
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private static final String urlPathFacts = "/facts";
    private static final String content = """
                        {
                        "translations": [
                          {
                            "language": {
                              "id": 1,
                              "code": "en"
                            },
                            "content": "dummy object"
                          }
                        ],
                        "habit": {
                          "id": 1
                        }
                      }
            """;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void getRandomFactByHabitId_WithValidHabitId_StatusOk() throws Exception {
        Long habitId = 1L;
        String language = "en";
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO();
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, language)).thenReturn(languageTranslationDTO);

        mockMvc.perform(get(urlPathFacts + "/random/{habitId}", habitId)
                        .param("locale", language)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(habitId, language);
    }

    @Test
    void getHabitFactOfTheDay_WithValidLanguageId_StatusOk() throws Exception {
        Long languageId = 1L;
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO();
        when(habitFactService.getHabitFactOfTheDay(languageId)).thenReturn(languageTranslationDTO);

        mockMvc.perform(get(urlPathFacts + "/dayFact/{languageId}", languageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitFactService).getHabitFactOfTheDay(languageId);
    }

    @Test
    void getAllHabitFacts_StatusOk() throws Exception {
        int page = 0;
        int size = 10;
        int totalPages = 20;
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO();
        PageableDto<LanguageTranslationDTO> pageableDto = new PageableDto<>(List.of(languageTranslationDTO), size, page, totalPages);
        Mockito.when(habitFactService.getAllHabitFacts(any(), anyString())).thenReturn(pageableDto);

        mockMvc.perform(get(urlPathFacts)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("locale", "en")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitFactService).getAllHabitFacts(any(), anyString());
    }

    @Test
    void deleteHabitFact_WithValidId_StatusOk() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete(urlPathFacts + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitFactService).delete(id);
    }

    @Test
    public void saveHabitFact_WithValidBodyRequest_StatusOk() throws Exception {
        HabitFactVO habitFactVO = new HabitFactVO();
        Mockito.when(habitFactService.save(any(HabitFactPostDto.class))).thenReturn(habitFactVO);

        mockMvc.perform(post("/facts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        verify(habitFactService).save(any(HabitFactPostDto.class));
    }

    @Test
    public void updateHabitFact_WithExistedId_StatusOk() throws Exception {
        Long id = 1L;
        HabitFactVO habitFactVO = new HabitFactVO();
        Mockito.when(habitFactService.update(any(HabitFactUpdateDto.class), Mockito.eq(id))).thenReturn(habitFactVO);

        mockMvc.perform(put(urlPathFacts + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        verify(habitFactService).update(any(HabitFactUpdateDto.class), Mockito.eq(id));
    }

    @Test
    void deleteHabitFact_WithNotExistedId_BadRequest() throws Exception {
        Long invalidId = 999L;
        Mockito.doThrow(new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID + invalidId)).when(habitFactService).delete(invalidId);

        mockMvc.perform(delete("/facts/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(habitFactService).delete(invalidId);
    }
}

