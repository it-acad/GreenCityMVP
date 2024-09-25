package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EcoNewsCommentDtoMapperTest {

    @InjectMocks
    private EcoNewsCommentDtoMapper mapper;

    @Test
    public void convertDeletedStatusTest() {
        // Arrange
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment()
                .setDeleted(true);

        EcoNewsCommentDto expected = EcoNewsCommentDto.builder()
                .id(ecoNewsComment.getId())
                .modifiedDate(ecoNewsComment.getModifiedDate())
                .status(CommentStatus.DELETED)
                .build();

        // Act
        EcoNewsCommentDto actual = this.mapper.convert(ecoNewsComment);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void convertOriginalStatusTest() {
        // Arrange
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment()
                .setModifiedDate(ModelUtils.localDateTime)
                .setCreatedDate(ModelUtils.localDateTime)
                .setUsersLiked(new HashSet<>());


        EcoNewsCommentDto expected = getEcoNewsCommentDto(ecoNewsComment,CommentStatus.ORIGINAL);

        // Act
        EcoNewsCommentDto actual = this.mapper.convert(ecoNewsComment);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void convertEditStatusTest() {
        // Arrange
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment()
                .setCreatedDate(ModelUtils.localDateTime)
                .setModifiedDate(ModelUtils.localDateTime.plusMinutes(1))
                .setUsersLiked(new HashSet<>());

        EcoNewsCommentDto expected = getEcoNewsCommentDto(ecoNewsComment,CommentStatus.EDITED);

        // Act
        EcoNewsCommentDto actual = this.mapper.convert(ecoNewsComment);

        // Assert
        assertEquals(expected, actual);
    }

    private static EcoNewsCommentDto getEcoNewsCommentDto(EcoNewsComment ecoNewsComment,CommentStatus commentStatus) {
        return EcoNewsCommentDto.builder()
                .id(ecoNewsComment.getId())
                .modifiedDate(ecoNewsComment.getModifiedDate())
                .status(commentStatus)
                .text(ecoNewsComment.getText())
                .author(EcoNewsCommentAuthorDto.builder()
                        .id(ecoNewsComment.getUser().getId())
                        .name(ecoNewsComment.getUser().getName())
                        .userProfilePicturePath(ecoNewsComment.getUser().getProfilePicturePath())
                        .build())
                .likes(ecoNewsComment.getUsersLiked().size())
                .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
                .build();
    }
}
