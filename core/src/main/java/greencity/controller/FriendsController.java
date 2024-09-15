package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.service.SearchService;
import greencity.service.UserService;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/friends")
public class FriendsController {
    private final SearchService searchService;
    private final UserService userService;

    @PostMapping("/{friendId}")
    public void addFriend(@CurrentUser UserVO currentUser, @PathVariable long friendId) {
        userService.addFriend(currentUser.getId(), friendId);
    }

    @PatchMapping("/{friendId}/acceptFriend")
    public void acceptFriendshipInvitation(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.acceptFriendshipInvitation(currentUser.getId(), friendId);
    }

    @DeleteMapping("/{friendId}/cancelFriend")
    public void cancelFriendshipInvitation(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.cancelFriendshipInvitation(currentUser.getId(), friendId);
    }

    @DeleteMapping("/{friendId}/declineFriend")
    public void declineFriendshipInvitation(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.declineFriendshipInvitation(currentUser.getId(), friendId);
    }

    @DeleteMapping("/{friendId}")
    public void deleteFriend(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.deleteFriend(currentUser.getId(), friendId);
    }

    @PageableAsQueryParam
    @GetMapping("/friendRequests")
    public ResponseEntity<PageableAdvancedDto<FriendCardDtoResponse>> getFriendsRequests(@CurrentUser UserVO currentUser,
                                                                                         Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriendshipRequests(currentUser.getId(), pageable));
    }

    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<PageableAdvancedDto<FriendCardDtoResponse>> searchFriends(@CurrentUser UserVO currentUser,
                                                                                    Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.searchFriends(currentUser.getId(), pageable));
    }

    @PageableAsQueryParam
    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableAdvancedDto<FriendCardDtoResponse>> searchNotFriendsYet(@CurrentUser UserVO currentUser,
                                                                                          @RequestParam
                                                                                          @Size(min = 1, max = 30, message = "Query should be between 1 and 30 characters") String name,
                                                                                          @RequestParam(required = false) String city, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.searchNotFriendsYet(currentUser.getId(), name, city, pageable));
    }
}
