package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.service.LanguageService;
import greencity.service.SearchService;
import greencity.validator.LanguageValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {
    private static final String searchControllerLink = "/search";
    private MockMvc mockMvc;

    @InjectMocks
    SearchController searchController;

    @Mock
    SearchService searchService;

    @Mock
    private LanguageService languageService;

    @BeforeEach
    public void setup() {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en", "ua", "ru"));

        LanguageValidator languageValidator = new LanguageValidator();
        ReflectionTestUtils.setField(languageValidator, "languageService", languageService);
        languageValidator.initialize(null);

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == LanguageValidator.class) {
                    return (T) languageValidator;
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
            }
        });
        validatorFactoryBean.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setValidator(validatorFactoryBean)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void search() throws Exception {
        SearchResponseDto searchResponseDto = SearchResponseDto.builder()
                .ecoNews(List.of())
                .countOfResults(0L)
                .build();

        when(searchService.search(anyString(), any())).thenReturn(searchResponseDto);

        mockMvc.perform(get(searchControllerLink)
                        .param("searchQuery", "test")
                        .locale(Locale.ENGLISH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(searchService).search(anyString(), anyString());
    }

    @Test
    void searchEcoNews() throws Exception {
        PageableDto<SearchNewsDto> pageableDto = new PageableDto<>(
                List.of(
                        new SearchNewsDto(1L, "Test Title", new EcoNewsAuthorDto(), ZonedDateTime.now()
                                , List.of("tag1", "tag2"))
                ),
                1L, 0, 1
        );

        when(searchService.searchAllNews(any(), anyString(), anyString())).thenReturn(pageableDto);

        mockMvc.perform(get(searchControllerLink + "/econews")
                        .param("searchQuery", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .locale(Locale.ENGLISH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(searchService).searchAllNews(any(Pageable.class), anyString(), anyString());
    }

    @Test
    void searchEventsTest() throws Exception {
        String searchQuery = "test";
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(searchControllerLink + "/events")
                                .param("searchQuery",searchQuery)
                                .param("page", String.valueOf(pageNumber))
                                .param("size", String.valueOf(pageSize))
                                .locale(Locale.ENGLISH))
                .andExpect(status().isOk());

        verify(searchService).searchAllEvents(pageable, searchQuery, Locale.ENGLISH.getLanguage());
    }
}