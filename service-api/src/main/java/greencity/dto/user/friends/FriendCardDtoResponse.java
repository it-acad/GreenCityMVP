package greencity.dto.user.friends;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FriendCardDtoResponse {
    @NotEmpty
    private Long id;

    private String avatarLink;

    @NotEmpty
    private String name;

    private double personalRate;

    private String city;

    private int mutualFriends;
}