package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.replytocomment.ReplyToCommentRequestDto;
import greencity.dto.replytocomment.ReplyToCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ReplyToCommentService;
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
public class ReplyToCommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReplyToCommentService replyToCommentService;

    @Mock
    private Validator mockValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReplyToCommentController replyToCommentController;
    private static final String initialUrl = "/reply-to-comment";
    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(replyToCommentController)
                .setValidator(mockValidator)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void saveReplyToComment_statusCreated() throws Exception {
        ReplyToCommentResponseDto replyToCommentDto = new ReplyToCommentResponseDto();
        replyToCommentDto.setContent("content");

        when(replyToCommentService.save(any(), anyLong(), anyLong())).thenReturn(new ReplyToCommentResponseDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(initialUrl + "/reply/" + 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isCreated());

        verify(replyToCommentService).save(any(), anyLong(), anyLong());
    }

    @Test
    void saveReplyToComment_withoutContent_statusBadRequest() throws Exception {
        ReplyToCommentRequestDto replyToCommentDto = new ReplyToCommentRequestDto();

        mockMvc.perform(post(initialUrl + "/reply/" + 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());
        verify(replyToCommentService, never()).save(any(ReplyToCommentRequestDto.class), anyLong(), anyLong());
    }

    @Test
    void updateReplyToComment_statusOk() throws Exception {
        ReplyToCommentResponseDto replyToCommentDto = new ReplyToCommentResponseDto();
        replyToCommentDto.setContent("content");

        when(replyToCommentService.update(any(), anyLong())).thenReturn(new ReplyToCommentResponseDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isOk());

        verify(replyToCommentService).update(any(), anyLong());
    }

    @Test
    void updateReplyToComment_withoutContent_statusBadRequest() throws Exception {
        ReplyToCommentRequestDto replyToCommentDto = new ReplyToCommentRequestDto();

        mockMvc.perform(patch(initialUrl)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());

        verify(replyToCommentService, never()).update(any(ReplyToCommentRequestDto.class), anyLong());
    }

    @Test
    void deleteReplyToComment_statusOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl + "/delete/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllReplies_statusOk() throws Exception {
        when(replyToCommentService.findAllByCommentId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(initialUrl + "/allReplies/1"))
                .andExpect(status().isOk());
    }
}
