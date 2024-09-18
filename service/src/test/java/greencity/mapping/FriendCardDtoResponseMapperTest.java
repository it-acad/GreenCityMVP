package greencity.mapping;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class FriendCardDtoResponseMapperTest {

    @InjectMocks
    private FriendCardDtoResponseMapper mapper;

    @Test
    void convertTest() {
        //Arrange
        User user = ModelUtils.getUser();
        user.setRating(0.0);
        FriendCardDtoResponse expected = FriendCardDtoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profilePicturePath(user.getProfilePicturePath())
                .personalRate(user.getRating())
                .city(user.getCity())
                .mutualFriends(0)
                .build();

        // Act
        FriendCardDtoResponse actual = mapper.convert(user);

        // Assert
        assertEquals(expected, actual);
    }
}
