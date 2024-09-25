package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.*;
import static greencity.enums.UserStatus.ACTIVATED;
import static greencity.enums.UserStatus.CREATED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    private UserVO userVO = UserVO.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastActivityTime(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .build();

    @Test
    void findByIdTest() {
        Long id = 1L;

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVO.class)).thenReturn(userVO);
        assertEquals(userVO, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(WrongIdException.class, () -> userService.checkIfTheUserIsOnline(null));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsTrueTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));

        assertTrue(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsFalseTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void checkUpdatableUserTest() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(modelMapper.map(any(), any())).thenReturn(userVO);
        Exception exception = assertThrows(BadUpdateRequestException.class, () -> {
            userService.checkUpdatableUser(1L, "email");
        });
        assertEquals(ErrorMessage.USER_CANT_UPDATE_HIMSELF, exception.getMessage());
    }

    @Test
    void getInitialsByIdTest() {
        when(userRepo.findById(any())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(modelMapper.map(any(), any())).thenReturn(userVO);
        assertEquals("TT", userService.getInitialsById(12L));
        userVO.setName("Taras");
        assertEquals("T", userService.getInitialsById(12L));
    }

    @Test
    void testFindByEmail() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);

        UserVO actual = userService.findByEmail(TEST_EMAIL);

        assertEquals(TEST_USER_VO, actual);

        verify(userRepo).findByEmail(TEST_EMAIL);
        verify(modelMapper).map(TEST_USER, UserVO.class);
    }

    @Test
    void testFindByEmailReturnNull() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        UserVO actual = userService.findByEmail(TEST_EMAIL);

        assertNull(actual);

        verify(userRepo).findByEmail(TEST_EMAIL);
    }

    @Test
    void testFindNotDeactivatedByEmail() {
        when(userRepo.findNotDeactivatedByEmail(TEST_EMAIL))
            .thenReturn(Optional.of(TEST_USER));
        when(modelMapper.map(Optional.of(TEST_USER), UserVO.class))
            .thenReturn(TEST_USER_VO);

        Optional<UserVO> actual = userService.findNotDeactivatedByEmail(TEST_EMAIL);

        assertEquals(Optional.of(TEST_USER_VO), actual);
    }

    @Test
    void testFindIdByEmail() {
        when(userRepo.findIdByEmail(TEST_EMAIL)).thenReturn(Optional.of(1L));

        Long actual = userService.findIdByEmail(TEST_EMAIL);

        assertEquals(1L, actual);
    }

    @Test
    void testFindIdByEmailThrowsException() {
        when(userRepo.findIdByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(WrongEmailException.class,
            () -> userService.findIdByEmail(TEST_EMAIL));
    }

    @Test
    void testUpdateUserLastActivityTime() {
        Date date = new Date();

        doNothing().when(userRepo).updateUserLastActivityTime(1L, date);

        userService.updateUserLastActivityTime(1L, date);

        verify(userRepo).updateUserLastActivityTime(1L, date);
    }

    @Test
    void testUpdateStatus() {
        when(userRepo.findByEmail(TEST_EMAIL_2)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(TEST_USER_ROLE_USER));
        when(modelMapper.map(TEST_USER_ROLE_USER, UserVO.class)).thenReturn(TEST_USER_VO_ROLE_USER);
        doNothing().when(userRepo).updateUserStatus(2L, String.valueOf(UserStatus.CREATED));
        when(modelMapper.map(TEST_USER_VO_ROLE_USER, UserStatusDto.class)).thenReturn(TEST_USER_STATUS_DTO);

        UserStatusDto actual = userService.updateStatus(2L, CREATED, TEST_EMAIL_2);

        assertEquals(TEST_USER_STATUS_DTO, actual);

        verify(userRepo, times(2)).findByEmail(anyString());
        verify(modelMapper, times(4)).map(any(User.class), eq(UserVO.class));
        verify(userRepo, times(2)).findById(anyLong());
        verify(userRepo).updateUserStatus(2L, String.valueOf(CREATED));
        verify(modelMapper).map(TEST_USER_VO_ROLE_USER, UserStatusDto.class);
    }

    @Test
    void testUpdateStatusThrowsBadUpdateRequestException() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);

        assertThrows(BadUpdateRequestException.class,
            () -> userService.updateStatus(1L, CREATED, TEST_EMAIL));
    }

    @Test
    void testUpdateStatusThrowsLowRoleLevelException() {
        when(userRepo.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);
        when(userRepo.findById(2L)).thenReturn(Optional.ofNullable(TEST_USER));
        when(modelMapper.map(TEST_USER, UserVO.class)).thenReturn(TEST_USER_VO);

        assertThrows(LowRoleLevelException.class,
            () -> userService.updateStatus(2L, CREATED, TEST_EMAIL));
    }

    @Test
    void getAllUsersByCriteriaTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<UserManagementVO> managementVOsList = new ArrayList<>();
        UserManagementVO userManagementVO = ModelUtils.getUserManagementVO();
        managementVOsList.add(userManagementVO);
        Page<UserManagementVO> page = new PageImpl<>(managementVOsList, pageable, 1);
        when(userRepo.findAllManagementVo(any(greencity.repository.options.UserFilter.class), eq(pageable)))
            .thenReturn(page);

        // when
        PageableDto<UserManagementVO> allUsersByCriteria =
            userService.getAllUsersByCriteria("Test", "ROLE_ADMIN", "ACTIVATED", pageable);

        // then
        assertTrue(allUsersByCriteria.getPage().contains(userManagementVO));
        verify(userRepo, times(1)).findAllManagementVo(any(greencity.repository.options.UserFilter.class),
            eq(pageable));
    }

    @Test
    public void addFriend_UserLoggedInAndFriendIdIsValid_ThenUserRepoAddFriendshipRequestCallsOnceWithArguments() throws Exception {
        final long validFriendId = 2L;

        userService.addFriend(userVO.getId(), validFriendId);

        verify(userRepo, times(1)).existsFriendshipById(userVO.getId(), validFriendId);
        verify(userRepo, times(1)).addFriendshipRequest(userVO.getId(), validFriendId);
    }

    @Test
    public void addFriend_UserLoggedInAndFriendIdEqualToCurrentUserId_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = userVO.getId();

        assertThatThrownBy(() -> userService.addFriend(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(1)).existsFriendshipById(userVO.getId(), notValidFriendId);
    }

    @Test
    public void addFriend_UserLoggedInAndFriendIdAlreadyAddedAsFriend_ThenExceptionThrown() throws Exception {
        final long alreadyAddedFriendId = 5;
        when(userRepo.existsFriendshipById(userVO.getId(), alreadyAddedFriendId)).thenReturn(true);

        assertThatThrownBy(() -> userService.addFriend(userVO.getId(), alreadyAddedFriendId))
                .isInstanceOf(FriendAlreadyAddedException.class);

        verify(userRepo, times(1)).existsFriendshipById(userVO.getId(), alreadyAddedFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendIdIsValid_ThenUserRepoAddFriendCallsOnceWithArguments() throws Exception {
        final long validFriendId = 2L;
        when(userRepo.existsFriendshipById(userVO.getId(), validFriendId)).thenReturn(false);
        when(userRepo.removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), validFriendId)).thenReturn(1);

        userService.acceptFriendshipInvitation(userVO.getId(), validFriendId);

        verify(userRepo, times(1)).existsFriendshipById(userVO.getId(), validFriendId);
        verify(userRepo, times(1)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), validFriendId);
        verify(userRepo, times(1)).addFriend(userVO.getId(), validFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendIdEqualToCurrentUserId_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = userVO.getId();

        assertThatThrownBy(() -> userService.acceptFriendshipInvitation(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(0)).existsFriendshipById(userVO.getId(), notValidFriendId);
        verify(userRepo, times(0)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), notValidFriendId);
        verify(userRepo, times(0)).addFriend(userVO.getId(), notValidFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendAlreadyAdded_ThenExceptionThrown() throws Exception {
        final long alreadyAddedFriendId = 5L;
        when(userRepo.existsFriendshipById(userVO.getId(), alreadyAddedFriendId)).thenReturn(true);

        assertThatThrownBy(() -> userService.acceptFriendshipInvitation(userVO.getId(), alreadyAddedFriendId))
                .isInstanceOf(FriendAlreadyAddedException.class);

        verify(userRepo, times(1)).existsFriendshipById(userVO.getId(), alreadyAddedFriendId);
        verify(userRepo, times(0)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), alreadyAddedFriendId);
        verify(userRepo, times(0)).addFriend(userVO.getId(), alreadyAddedFriendId);
    }

    @Test
    public void acceptFriendshipInvitation_UserLoggedInAndFriendIdIsNotValid_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = 500L;
        when(userRepo.removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), notValidFriendId)).thenReturn(0);

        assertThatThrownBy(() -> userService.acceptFriendshipInvitation(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(1)).existsFriendshipById(userVO.getId(), notValidFriendId);
        verify(userRepo, times(1)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), notValidFriendId);
        verify(userRepo, times(0)).addFriend(userVO.getId(), notValidFriendId);
    }

    @Test
    public void cancelFriendshipInvitation_UserLoggedInAndInvitationExists_ThenUserRepoRemoveFromFriendshipRequestsByInvitingUserIdCallsOnce() throws Exception {
        final long validFriendId = 2L;
        when(userRepo.removeFromFriendshipRequestsByInvitingUserId(userVO.getId(), validFriendId)).thenReturn(1);

        userService.cancelFriendshipInvitation(userVO.getId(), validFriendId);

        verify(userRepo, times(1)).removeFromFriendshipRequestsByInvitingUserId(userVO.getId(), validFriendId);
    }

    @Test
    public void cancelFriendshipInvitation_UserLoggedInAndInvitedFriendIdEqualToCurrentUserId_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = userVO.getId();

        assertThatThrownBy(() -> userService.cancelFriendshipInvitation(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(0)).removeFromFriendshipRequestsByInvitingUserId(userVO.getId(), notValidFriendId);
    }

    @Test
    public void cancelFriendshipInvitation_UserLoggedInAndInvitationNotExists_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = 500L;
        when(userRepo.removeFromFriendshipRequestsByInvitingUserId(userVO.getId(), notValidFriendId)).thenReturn(0);

        assertThatThrownBy(() -> userService.cancelFriendshipInvitation(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(1)).removeFromFriendshipRequestsByInvitingUserId(userVO.getId(), notValidFriendId);
    }

    ///declineFriendshipInvitation
    @Test
    public void declineFriendshipInvitation_UserLoggedInAndInvitationExists_ThenUserRepoRemoveFromFriendshipRequestsByAcceptingUserIdCallsOnce() throws Exception {
        final long validFriendId = 2L;
        when(userRepo.removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), validFriendId)).thenReturn(1);

        userService.declineFriendshipInvitation(userVO.getId(), validFriendId);

        verify(userRepo, times(1)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), validFriendId);
    }

    @Test
    public void declineFriendshipInvitation_UserLoggedInAndInvitedFriendIdEqualToCurrentUserId_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = userVO.getId();

        assertThatThrownBy(() -> userService.declineFriendshipInvitation(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(0)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), notValidFriendId);
    }

    @Test
    public void declineFriendshipInvitation_UserLoggedInAndInvitationNotExists_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = 500L;
        when(userRepo.removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), notValidFriendId)).thenReturn(0);

        assertThatThrownBy(() -> userService.declineFriendshipInvitation(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(1)).removeFromFriendshipRequestsByAcceptingUserId(userVO.getId(), notValidFriendId);
    }

    @Test
    public void deleteFriend_UserLoggedInAndFriendIdIsValid_ThenUserRepoRemoveFriendCallsOnce() throws Exception {
        final long validFriendId = 2L;
        when(userRepo.existsFriendshipById(userVO.getId(), validFriendId)).thenReturn(true);

        userService.deleteFriend(userVO.getId(), validFriendId);

        verify(userRepo, times(1)).removeFriend(userVO.getId(), validFriendId);
    }

    @Test
    public void deleteFriend_UserLoggedInAndFriendshipDoesNotExists_ThenExceptionThrown() throws Exception {
        final long notValidFriendId = 500L;
        when(userRepo.existsFriendshipById(userVO.getId(), notValidFriendId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteFriend(userVO.getId(), notValidFriendId))
                .isInstanceOf(BadRequestException.class);

        verify(userRepo, times(0)).removeFriend(userVO.getId(), notValidFriendId);
    }

    @Test
    public void getFriendsRequests_UserLoggedInAndRequestsExists_ThenReturnPageOfUser() throws Exception {
        final long currentUserId = 1L;
        final User friend = ModelUtils.getUser();
        Page<User> page = new PageImpl<>(List.of(friend), PageRequest.of(0, 10), 1);
        FriendCardDtoResponse friendCardDtoResponse = new FriendCardDtoResponse(friend.getId(), friend.getName(), friend.getProfilePicturePath(), 0.0, friend.getCity(), 0);
        when(userRepo.getFriendshipRequestsByUserId(userVO.getId(), PageRequest.of(0, 10))).thenReturn(page);
        when(userRepo.getAmountOfMutualFriends(currentUserId, friend.getId())).thenReturn(0);
        when(modelMapper.map(friend, FriendCardDtoResponse.class)).thenReturn(friendCardDtoResponse);

        PageableAdvancedDto<FriendCardDtoResponse> result = userService.getFriendshipRequests(userVO.getId(), PageRequest.of(0, 10));

        assertFalse(result.getPage().isEmpty());
    }
}
