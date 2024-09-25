package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.FriendAlreadyAddedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.SearchService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class FriendsControllerTest {
    private static final String friendsLink = "/friends";
    private MockMvc mockMvc;
    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final Principal principal = ModelUtils.getPrincipal();
    private final UserVO userVO = ModelUtils.getUserVO();

    @Mock
    private UserService userService;
    @Mock
    private SearchService searchService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private FriendsController friendsController;
    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(friendsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

    }

    @Test
    public void addFriend_UserLoggedInAndFriendIdIsValid_thenReturnStatus200() throws Exception {
        final long validFriendId = 2L;

        mockMvc.perform(post(friendsLink + "/{friendId}", validFriendId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService, times(1)).addFriend(userVO.getId(), validFriendId);
    }

    @Test
    public void addFriend_UserLoggedInAndFriendIdEqualToCurrentUserId_thenReturnStatus400() throws Exception {
        final long notValidFriendId = userVO.getId();
        doThrow(BadRequestException.class).when(userService).addFriend(userVO.getId(), notValidFriendId);

        mockMvc.perform(post(friendsLink + "/{friendId}", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).addFriend(userVO.getId(), notValidFriendId);
    }

    @Test
    public void addFriend_UserLoggedInAndFriendIdIsNotValid_thenReturnStatus400() throws Exception {
        final long notValidFriendId = 4L;
        doThrow(FriendAlreadyAddedException.class).when(userService).addFriend(userVO.getId(), notValidFriendId);

        mockMvc.perform(post(friendsLink + "/{friendId}", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).addFriend(userVO.getId(), notValidFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendIdIsValid_thenReturnStatus200() throws Exception {
        final long validFriendId = 2L;

        mockMvc.perform(patch(friendsLink + "/{friendId}/acceptFriend", validFriendId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService, times(1)).acceptFriendshipInvitation(userVO.getId(), validFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendIdEqualToCurrentUserId_thenReturnStatus400() throws Exception {
        final long notValidFriendId = userVO.getId();
        doThrow(BadRequestException.class).when(userService).acceptFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(patch(friendsLink + "/{friendId}/acceptFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).acceptFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendAlreadyAdded_thenReturnStatus400() throws Exception {
        final long notValidFriendId = 4L;
        doThrow(FriendAlreadyAddedException.class).when(userService).acceptFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(patch(friendsLink + "/{friendId}/acceptFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).acceptFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendIdIsNotValid_thenReturnStatus400() throws Exception {
        final long notValidFriendId = 500L;
        doThrow(BadRequestException.class).when(userService).acceptFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(patch(friendsLink + "/{friendId}/acceptFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).acceptFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void cancelFriendshipInvitation_UserLoggedInAndInvitationExists_thenReturnStatus200() throws Exception {
        final long validFriendId = 2L;

        mockMvc.perform(delete(friendsLink + "/{friendId}/cancelFriend", validFriendId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService, times(1)).cancelFriendshipInvitation(userVO.getId(), validFriendId);
    }

    @Test
    public void cancelFriendshipInvitation_UserLoggedInAndInvitedFriendIdEqualToCurrentUserId_thenReturnStatus400() throws Exception {
        final long notValidFriendId = userVO.getId();
        doThrow(BadRequestException.class).when(userService).cancelFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(delete(friendsLink + "/{friendId}/cancelFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).cancelFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void cancelFriendshipInvitation_UserLoggedInAndInvitationNotExists_thenReturnStatus400() throws Exception {
        final long notValidFriendId = 500L;
        doThrow(FriendAlreadyAddedException.class).when(userService).cancelFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(delete(friendsLink + "/{friendId}/cancelFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).cancelFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void declineFriendshipInvitation_UserLoggedInAndInvitationExists_thenReturnStatus200() throws Exception {
        final long validFriendId = 2L;

        mockMvc.perform(delete(friendsLink + "/{friendId}/declineFriend", validFriendId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService, times(1)).declineFriendshipInvitation(userVO.getId(), validFriendId);
    }

    @Test
    public void declineFriendshipInvitation_UserLoggedInAndInvitedFriendIdEqualToCurrentUserId_thenReturnStatus400() throws Exception {
        final long notValidFriendId = userVO.getId();
        doThrow(BadRequestException.class).when(userService).declineFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(delete(friendsLink + "/{friendId}/declineFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).declineFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void declineFriendshipInvitation_UserLoggedInAndInvitationNotExists_thenReturnStatus400() throws Exception {
        final long notValidFriendId = 500L;
        doThrow(BadRequestException.class).when(userService).declineFriendshipInvitation(userVO.getId(), notValidFriendId);


        mockMvc.perform(delete(friendsLink + "/{friendId}/declineFriend", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).declineFriendshipInvitation(userVO.getId(), notValidFriendId);
    }

    @Test
    public void deleteFriend_UserLoggedInAndFriendIdIsValid_thenReturnStatus200() throws Exception {
        final long validFriendId = 2L;

        mockMvc.perform(delete(friendsLink + "/{friendId}", validFriendId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteFriend(userVO.getId(), validFriendId);
    }

    @Test
    public void deleteFriend_UserLoggedInAndFriendshipDoesNotExists_thenReturnStatus400() throws Exception {
        final long notValidFriendId = 500L;
        doThrow(BadRequestException.class).when(userService).deleteFriend(userVO.getId(), notValidFriendId);

        mockMvc.perform(delete(friendsLink + "/{friendId}", notValidFriendId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).deleteFriend(userVO.getId(), notValidFriendId);
    }

    @Test
    public void getFriendsRequests_UserLoggedInAndRequestsExists_thenReturnStatus200() throws Exception {
        List<FriendCardDtoResponse> friendsDto = List.of(new FriendCardDtoResponse(
                1L, "JohnDoe", "", 0.0, "Tacoma", 4));
        PageableAdvancedDto<FriendCardDtoResponse> result = new PageableAdvancedDto<>(
                friendsDto, 10, 0, 1, 0, false, true, true, true);
        when(userService.getFriendshipRequests(userVO.getId(), PageRequest.of(0, 10))).thenReturn(result);

        mockMvc.perform(get(friendsLink + "/friendRequests")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService, times(1)).getFriendshipRequests(userVO.getId(), PageRequest.of(0, 10));
    }

    @Test
    public void searchFriends_UserLoggedInAndFriendsExist_thenReturnStatus200() throws Exception {
        List<FriendCardDtoResponse> friendsDto = List.of(new FriendCardDtoResponse(
                1L, "JohnDoe", "", 0.0, "Tacoma", 4));
        PageableAdvancedDto<FriendCardDtoResponse> result = new PageableAdvancedDto<>(
                friendsDto, 10, 0, 1, 0, false, true, true, true);
        when(searchService.searchFriends(userVO.getId(),  PageRequest.of(0, 10))).thenReturn(result);

        mockMvc.perform(get(friendsLink)
                        .param("page", "0")
                        .param("size", "10")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(searchService, times(1)).searchFriends(userVO.getId(),  PageRequest.of(0, 10));
    }

    @Test
    public void searchNotFriendsYet_UserLoggedInAndNotYetFriendsExist_thenReturnStatus200() throws Exception {
        List<FriendCardDtoResponse> friendsDto = List.of(new FriendCardDtoResponse(
                1L, "JohnDoe", "", 0.0, "Tacoma", 4));
        PageableAdvancedDto<FriendCardDtoResponse> result = new PageableAdvancedDto<>(
                friendsDto, 10, 0, 1, 0, false, true, true, true);
        when(searchService.searchNotFriendsYet(userVO.getId(), "John", "Tacoma", PageRequest.of(0, 10))).thenReturn(result);

        mockMvc.perform(get(friendsLink + "/not-friends-yet")
                        .param("page", "0")
                        .param("size", "10")
                        .param("name", "John")
                        .param("city", "Tacoma")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(searchService, times(1)).searchNotFriendsYet(userVO.getId(), "John", "Tacoma", PageRequest.of(0, 10));
    }

}
