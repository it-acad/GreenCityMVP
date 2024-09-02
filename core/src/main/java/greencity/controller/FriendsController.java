package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.user.UserVO;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendsController {
    private final UserService userService;

    @PostMapping
    public void addFriend(@CurrentUser UserVO currentUser, @RequestBody long friendID) {
        userService.addFriend(currentUser.getId(), friendID);
    }

    @PatchMapping("/accept/{friendId}")
    public void acceptFriendshipInvitation(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.acceptFriendshipInvitation(currentUser.getId(), friendId);
    }

    @PatchMapping("/decline/{friendId}")
    public void declineFriendshipInvitation(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.declineFriendshipInvitation(currentUser.getId(), friendId);
    }

    @PatchMapping("/cancel/{friendId}")
    public void cancelFriendshipInvitation(@CurrentUser UserVO currentUser, @PathVariable Long friendId) {
        userService.cancelFriendshipInvitation(currentUser.getId(), friendId);
    }
}
