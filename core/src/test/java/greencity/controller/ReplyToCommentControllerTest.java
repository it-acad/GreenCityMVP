package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.replytocomment.ReplyToCommentDto;
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
        ReplyToCommentDto replyToCommentDto = new ReplyToCommentDto();
        replyToCommentDto.setContent("content");

        when(replyToCommentService.save(any(), anyLong(), anyLong())).thenReturn(new ReplyToCommentDto());
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(initialUrl + "/" + 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isCreated());

        verify(replyToCommentService).save(any(), anyLong(), anyLong());
    }

    @Test
    void saveReplyToComment_withoutContent_statusBadRequest() throws Exception {
        ReplyToCommentDto replyToCommentDto = new ReplyToCommentDto();

        mockMvc.perform(post(initialUrl + "/" + 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());
        verify(replyToCommentService, never()).save(any(ReplyToCommentDto.class), anyLong(), anyLong());
    }

    @Test
    void updateReplyToComment_statusOk() throws Exception {
        ReplyToCommentDto replyToCommentDto = new ReplyToCommentDto();
        replyToCommentDto.setContent("content");

        when(replyToCommentService.update(any(), anyLong())).thenReturn(new ReplyToCommentDto());
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
        ReplyToCommentDto replyToCommentDto = new ReplyToCommentDto();

        mockMvc.perform(patch(initialUrl)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(replyToCommentDto)))
                .andExpect(status().isBadRequest());

        verify(replyToCommentService, never()).update(any(ReplyToCommentDto.class), anyLong());
    }

    @Test
    void deleteReplyToComment_statusOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("replyToCommentId", "1"))
                .andExpect(status().isOk());

    }

    @Test
    void getAllReplies_statusOk() throws Exception {
        when(replyToCommentService.findAllByCommentId(anyLong())).thenReturn(null);

        mockMvc.perform(get(initialUrl + "/allReplies/1"))
                .andExpect(status().isOk());
    }
}
