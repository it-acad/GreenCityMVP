package greencity.mapping;


import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static greencity.ModelUtils.getUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EcoNewsCommentVOMapperTest {

    @InjectMocks
    private EcoNewsCommentVOMapper mapper;

    @Test
    public void convertWithParentCommentTest() {
        // Arrange
        EcoNewsComment comment = ModelUtils.getEcoNewsComment()
                .setUsersLiked(new HashSet<>(List.of(ModelUtils.getUser())));
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment()
                .setUsersLiked(new HashSet<>(List.of(ModelUtils.getUser())))
                .setParentComment(comment);

        EcoNewsCommentVO expected = convertWithNullParentComment(ecoNewsComment);
        expected.setParentComment(convertWithNullParentComment(comment));

        // Act
        EcoNewsCommentVO actual = this.mapper.convert(comment);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void convertWithoutParentCommentTest() {
        // Arrange
        EcoNewsComment comment = ModelUtils.getEcoNewsComment()
                .setUsersLiked(new HashSet<>(List.of(getUser())));

        EcoNewsCommentVO expected = convertWithNullParentComment(comment);

        // Act
        EcoNewsCommentVO actual = this.mapper.convert(comment);

        // Assert
        assertEquals(expected, actual);
    }


    private EcoNewsCommentVO convertWithNullParentComment(EcoNewsComment ecoNewsComment) {
        return EcoNewsCommentVO.builder()
                .id(ecoNewsComment.getId())
                .user(UserVO.builder()
                        .id(ecoNewsComment.getUser().getId())
                        .role(ecoNewsComment.getUser().getRole())
                        .name(ecoNewsComment.getUser().getName())
                        .build())
                .modifiedDate(ecoNewsComment.getModifiedDate())
                .parentComment(null)
                .text(ecoNewsComment.getText())
                .deleted(ecoNewsComment.isDeleted())
                .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
                .createdDate(ecoNewsComment.getCreatedDate())
                .usersLiked(ecoNewsComment.getUsersLiked().stream().map(user -> UserVO.builder()
                                .id(user.getId())
                                .build())
                        .collect(Collectors.toSet()))
                .ecoNews(EcoNewsVO.builder()
                        .id(ecoNewsComment.getEcoNews().getId())
                        .build())
                .build();
    }
}
