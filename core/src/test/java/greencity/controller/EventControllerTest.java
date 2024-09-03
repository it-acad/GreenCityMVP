package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    }

    @Test
    void testDeleteEvent_StatusIsOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doNothing().when(eventService).delete(EVENT_ID, userVO.getId());
        mockMvc.perform(delete("/events/" + EVENT_ID)
                        .principal(principal))
                .andExpect(status().isOk());
        verify(eventService).delete(EVENT_ID, userVO.getId());
    }

    @Test
    void testDeleteEvent_Fail_EventNotFound() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doThrow(new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND)).when(eventService).delete(EVENT_ID, userVO.getId());
        mockMvc.perform(delete("/events/" + EVENT_ID)
                        .principal(principal))
                .andExpect(status().isNotFound());
        verify(eventService).delete(EVENT_ID, userVO.getId());
    }

    @Test
    void testUpdateEvent_success() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
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
        when(userService.findByEmail(anyString())).thenReturn(userVO);
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