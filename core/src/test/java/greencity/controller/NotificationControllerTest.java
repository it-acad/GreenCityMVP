package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationController notificationController;
    private final String initialUrl = "/notifications";
    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void getUnreadNotifications_StatusIsOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(initialUrl + "/unread")
                .principal(principal))
                .andExpect(status().isOk());

        verify(notificationService).findAllByUserIdAndIsReadFalse(userVO.getId());
    }

    @Test
    void getAllNotifications_StatusIsOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(initialUrl + "/all")
                .principal(principal))
                .andExpect(status().isOk());

        verify(notificationService).findAllByUserId(userVO.getId());
    }

    @Test
    void markAsViewed_StatusIsOk() throws Exception {

        mockMvc.perform(post(initialUrl + "/markAsViewed/1"))
                .andExpect(status().isOk());

        verify(notificationService).markAsReadNotification(1L);
    }
}
