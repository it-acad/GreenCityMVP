package greencity.mapping;

import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class FriendCardDtoResponseMapper extends AbstractConverter<User, FriendCardDtoResponse> {
    @Override
    protected FriendCardDtoResponse convert(User user) {
        return FriendCardDtoResponse.builder()
                .id(user.getId())
                .avatarLink(user.getProfilePicturePath())
                .name(user.getName())
                .personalRate(user.getRating())
                .city(user.getCity())
                .build();
    }
}
