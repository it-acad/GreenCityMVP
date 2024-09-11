package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventCommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventCommentService replyToCommentService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentController replyToCommentController;
    private static final String initialUrl = "/events/1/comments";
    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.replyToCommentController)
                .setCustomArgumentResolvers(new UserArgumentResolver(this.userService, this.modelMapper))
                .build();
    }

    @Test
    void saveReplyToComment_statusCreated() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();
        replyToCommentDto.setText("content");

        when(this.replyToCommentService.saveReply(any(), anyLong(), anyLong(), anyLong())).thenReturn(new EventCommentDtoResponse());
        when(this.userService.findByEmail(anyString())).thenReturn(userVO);

        this.mockMvc.perform(post(initialUrl + "/reply/" + 1L)
                        .principal(this.principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isCreated());

        verify(this.replyToCommentService).saveReply(any(), anyLong(), anyLong(), anyLong());
    }

    @Test
    void saveReplyToComment_withoutContent_statusBadRequest() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();

        this.mockMvc.perform(post(initialUrl + "/reply/" + 1L)
                        .principal(this.principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());

        verify(this.replyToCommentService, never()).saveReply(any(EventCommentDtoRequest.class), anyLong(), anyLong(), anyLong());
    }

    @Test
    void updateReplyToComment_withoutContent_statusBadRequest() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();

        this.mockMvc.perform(patch(initialUrl + "/reply/{replyToCommentId}", 1L)
                        .principal(this.principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());

        verify(this.replyToCommentService, never()).updateReply(any(EventCommentDtoRequest.class), anyLong(), anyLong());
    }

    @Test
    void updateReplyToComment_statusOk() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();
        replyToCommentDto.setText("content");

        when(this.replyToCommentService.updateReply(any(), anyLong(), anyLong())).thenReturn(new EventCommentDtoResponse());
        when(this.userService.findByEmail(anyString())).thenReturn(userVO);

        this.mockMvc.perform(patch(initialUrl + "/reply/{replyToCommentId}", 1L)
                        .principal(this.principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isOk());

        verify(this.replyToCommentService).updateReply(any(), eq(1L), anyLong());
    }

    @Test
    void deleteReplyToComment_statusOk() throws Exception {

        when(this.userService.findByEmail(anyString())).thenReturn(userVO);

        this.mockMvc.perform(delete(initialUrl + "/reply/{replyToCommentId}", 1L)
                        .principal(this.principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllReplies_statusOk() throws Exception {
        when(this.replyToCommentService.findAllReplyByCommentId(anyLong())).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get(initialUrl + "/allReplies/1"))
                .andExpect(status().isOk());
    }
}
