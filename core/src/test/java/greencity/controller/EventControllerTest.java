package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.entity.User;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
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
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    private static final String eventLink = "/events";
    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventService eventService;

    private MockMvc mockMvc;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }


    @Test
    public void getAll_ReturnStatusCode200() throws Exception {
        when(eventService.findAll()).thenReturn(new HashSet<>());

        mockMvc.perform(get(eventLink)
                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(eventService, times(1)).findAll();
    }

    @Test
    public void getAllEventsByUser_EventsExistsForCurrentUser_ReturnStatusCode200() throws Exception {
        User user = ModelUtils.getUser();
        when(eventService.findAllByUserId(user.getId())).thenReturn(new HashSet<>(Set.of(ModelUtils.getEventDto())));

        mockMvc.perform(get(eventLink + "/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventService, times(1)).findAllByUserId(user.getId());
    }
}
