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
import org.springframework.validation.Validator;

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
    private Validator mockValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentController replyToCommentController;
    private static final String initialUrl = "/comments";
    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(replyToCommentController)
                .setValidator(this.mockValidator)
                .setCustomArgumentResolvers(new UserArgumentResolver(this.userService, this.modelMapper))
                .build();
    }

    @Test
    void saveReplyToComment_statusCreated() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();
        replyToCommentDto.setText("content");

        when(replyToCommentService.saveReply(any(), anyLong(), anyLong())).thenReturn(new EventCommentDtoResponse());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(initialUrl + "/reply/" + 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isCreated());

        verify(replyToCommentService).saveReply(any(), anyLong(), anyLong());
    }

    @Test
    void updateReplyToComment_withoutContent_statusBadRequest() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();

        mockMvc.perform(patch(initialUrl + "/reply/{replyToCommentId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());

        verify(replyToCommentService, never()).updateReply(any(EventCommentDtoRequest.class), anyLong(), anyLong());
    }

    @Test
    void updateReplyToComment_statusOk() throws Exception {
        EventCommentDtoRequest replyToCommentDto = new EventCommentDtoRequest();
        replyToCommentDto.setText("content");

        when(replyToCommentService.updateReply(any(), anyLong(), anyLong())).thenReturn(new EventCommentDtoResponse());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl + "/reply/{replyToCommentId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isOk());

        verify(replyToCommentService).updateReply(any(), eq(1L), anyLong());
    }

    @Test
    void deleteReplyToComment_statusOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl + "/reply/{replyToCommentId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllReplies_statusOk() throws Exception {
        when(replyToCommentService.findAllReplyByCommentId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(initialUrl + "/allReplies/1"))
                .andExpect(status().isOk());
    }
}
