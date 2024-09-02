package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventServiceImp;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventServiceImp eventService;
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
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private MockMvc mockMvc;
    private final Principal principal = ModelUtils.getPrincipal();
    private final UserVO userVO = ModelUtils.getUserVO();
    private static final Long EVENT_ID = 1L;
    private final MultipartFile[] images = {new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes())};
    private final String eventEditDtoJson = """
                {"eventTitle":" TEST2424",
                "description":"JETTA",
                "eventDayDetailsList":[{
                "id":"9",
                "eventDate":"1900-12-12",
                "eventStartTime":"11:00",
                "eventEndTime":"12:00",
                "isAllDateDuration":true,
                "isOnline":true,
                "isOffline":false,
                "offlinePlace":null,
                "onlinePlace":"GITHUB"}],
                "eventType":"OPEN",
                "imagePathList":[
                "/images/event1.jpg",
                "/images/event2.jpg"
                ]}
                """;
    private final MockMultipartFile eventEditDtoPart = new MockMultipartFile(
            "eventEditDto",
            null,
            MediaType.APPLICATION_JSON_VALUE,
            eventEditDtoJson.getBytes()
    );

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
        when(userService.findByEmail(anyString())).thenReturn(userVO);
    }

    @Test
    void testDeleteEvent_StatusIsOk() throws Exception {
        doNothing().when(eventService).delete(EVENT_ID, userVO.getId());
        mockMvc.perform(delete("/events/" + EVENT_ID)
                        .principal(principal))
                .andExpect(status().isOk());
        verify(eventService).delete(EVENT_ID, userVO.getId());
    }

    @Test
    void testDeleteEvent_Fail_EventNotFound() throws Exception {
        doThrow(new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND)).when(eventService).delete(EVENT_ID, userVO.getId());
        mockMvc.perform(delete("/events/" + EVENT_ID)
                        .principal(principal))
                .andExpect(status().isNotFound());
        verify(eventService).delete(EVENT_ID, userVO.getId());
    }

    @Test
    void testUpdateEvent_success() throws Exception {
        when(eventService.update(any(EventEditDto.class), eq(userVO.getId()), eq(EVENT_ID), any(MultipartFile[].class)))
                .thenReturn(any(EventDto.class));
        mockMvc.perform(multipart(HttpMethod.PUT,"/events/" + EVENT_ID)
                        .file("images", images[0].getBytes())
                        .file(eventEditDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(principal))
                .andExpect(status().isOk());
        verify(eventService).update(any(EventEditDto.class), eq(userVO.getId()), eq(EVENT_ID), any(MultipartFile[].class));
    }

    @Test
    void testUpdateEvent_eventNotFound() throws Exception {
        doThrow(new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND + EVENT_ID))
                .when(eventService).update(any(EventEditDto.class), anyLong(), anyLong(), any(MultipartFile[].class));

        mockMvc.perform(multipart(HttpMethod.PUT,"/events/" + EVENT_ID)
                        .file("images",images[0].getBytes())
                        .file(eventEditDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(principal))
                .andExpect(status().isNotFound());
        verify(eventService).update(any(EventEditDto.class), eq(userVO.getId()), eq(EVENT_ID), any(MultipartFile[].class));
    }
}

