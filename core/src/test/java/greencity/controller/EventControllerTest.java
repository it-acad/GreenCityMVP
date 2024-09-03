package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventServiceImpl;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventServiceImpl eventService;
    @Mock
    private UserService userService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Validator mockValidator;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private EventController eventController;
    private static final String eventLink = "/events";
    private MockMvc mockMvc;
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final Principal principal = ModelUtils.getPrincipal();
    private final UserVO userVO = ModelUtils.getUserVO();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes,
                        objectMapper))
                .setValidator(mockValidator)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void saveEvent_WithImages_ValidRequest_Created() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);


        MockMultipartFile eventCreationDtoPart = getMockMultipartFile();
        MockMultipartFile image1 = new MockMultipartFile("images", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 content".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 content".getBytes());

        EventDto savedEvent = new EventDto();

        when(eventService.saveEvent(any(EventCreationDtoRequest.class), any(List.class), anyString())).thenReturn(savedEvent);

        mockMvc.perform(multipart(eventLink)
                        .file(eventCreationDtoPart)
                        .file(image1)
                        .file(image2)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated());

        verify(eventService).saveEvent(any(EventCreationDtoRequest.class), anyList(), anyString());
    }

    @Test
    void saveEvent_WithoutImages_ValidRequest_Created() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        MockMultipartFile eventCreationDtoPart = getMockMultipartFile();

        EventDto savedEvent = new EventDto();

        when(eventService.saveEvent(any(EventCreationDtoRequest.class), eq(null), anyString())).thenReturn(savedEvent);

        mockMvc.perform(multipart(eventLink)
                        .file(eventCreationDtoPart)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated());

        verify(eventService).saveEvent(any(EventCreationDtoRequest.class), eq(null), anyString());
    }

    private static MockMultipartFile getMockMultipartFile() {
        String eventCreationDtoJson = "{\n" +
                "    \"eventTitle\": \"This is a title.\",\n" +
                "    \"description\": \"This is a description.\",\n" +
                "    \"datesLocations\": [\n" +
                "        {\n" +
                "            \"eventDate\": \"2024-09-09\",\n" +
                "            \"eventStartTime\": \"10:00\",\n" +
                "            \"eventEndTime\": \"12:00\",\n" +
                "            \"isAllDateDuration\": false,\n" +
                "            \"isOnline\": true,\n" +
                "            \"isOffline\": true,\n" +
                "            \"onlinePlace\": \"https://www.greencity.cx.ua/#/greenCity\",\n" +
                "            \"offlinePlace\": \"Offline Place\",\n" +
                "            \"latitude\": 0.001,\n" +
                "            \"longitude\": 0.001\n" +
                "        }\n" +
                "    ],\n" +
                "    \"eventType\": \"OPEN\"\n" +
                "}";
        return new MockMultipartFile("eventCreationDtoRequest", "", MediaType.APPLICATION_JSON_VALUE, eventCreationDtoJson.getBytes());
    }

}