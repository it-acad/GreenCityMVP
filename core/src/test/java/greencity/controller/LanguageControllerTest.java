package greencity.controller;

import greencity.config.SecurityConfig;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
public class LanguageControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LanguageController languageController;

    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
                .build();
    }

    @Test
    void getAllLanguageCodes() throws Exception {

        when(languageService.findAllLanguageCodes())
                .thenReturn(List.of());

        mockMvc.perform(get("/language"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(languageService).findAllLanguageCodes();
    }
}
