package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventCommentControllerTest {
    private static final String LINK = "/events/comments";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final Long eventId = 1L;
    private final Long commentId = 1L;
    private final Long commentCount = 5L;

    @Mock
    private EventCommentService eventCommentService;

    @InjectMocks
    private EventCommentController eventCommentController;

    private Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventCommentController).build();
    }

    private AddEventCommentDtoRequest createRequestDto(String text) {
        AddEventCommentDtoRequest requestDto = new AddEventCommentDtoRequest();
        requestDto.setText(text);
        return requestDto;
    }

    private AddEventCommentDtoResponse createResponseDto(Long id, String text) {
        return AddEventCommentDtoResponse.builder()
                .id(id)
                .text(text)
                .eventId(eventId)
                .build();
    }

    @Test
    @SneakyThrows
    void addComment() {
        AddEventCommentDtoRequest requestDto = createRequestDto("New comment");
        AddEventCommentDtoResponse responseDto = createResponseDto(eventId, "New comment");

        when(eventCommentService.addComment(anyLong(), any(AddEventCommentDtoRequest.class), any(UserVO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post(LINK + "/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(eventCommentService).addComment(anyLong(), any(AddEventCommentDtoRequest.class), any(UserVO.class));
    }

    @Test
    @SneakyThrows
    void getCommentsByEventId() {
        AddEventCommentDtoResponse commentResponse = createResponseDto(1L, "Sample comment");

        when(eventCommentService.getCommentsByEventId(anyLong()))
                .thenReturn(Collections.singletonList(commentResponse));

        mockMvc.perform(get(LINK + "/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventCommentService).getCommentsByEventId(anyLong());
    }

    @Test
    @SneakyThrows
    void getCommentById() {
        AddEventCommentDtoResponse commentResponse = createResponseDto(commentId, "Sample comment");

        when(eventCommentService.getCommentById(anyLong()))
                .thenReturn(commentResponse);

        mockMvc.perform(get(LINK + "/{commentId}/details", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventCommentService).getCommentById(anyLong());
    }

    @Test
    @SneakyThrows
    void showQuantityOfAddedComments() {
        when(eventCommentService.showQuantityOfAddedComments(anyLong()))
                .thenReturn(commentCount);

        mockMvc.perform(get(LINK + "/{eventId}/count", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventCommentService).showQuantityOfAddedComments(anyLong());
    }
}
