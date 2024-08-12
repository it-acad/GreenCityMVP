package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsMapperTest {

    private final EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();

    @Test
    void map() {
        // Arrange
        AddEcoNewsCommentDtoRequest expected = ModelUtils.getAddEcoNewsCommentDtoRequest();

        // Act
        AddEcoNewsCommentDtoRequest actual = UtilsMapper.map(this.ecoNewsComment, AddEcoNewsCommentDtoRequest.class);

        String expectedText = expected.getText();
        String actualText = actual.getText();

        // Assert
        assertEquals(expectedText, actualText);
    }

    @Test
    void mapAllToList() {
        // Arrange
        List<EcoNewsComment> ecoNewsCommentList = List.of(this.ecoNewsComment, this.ecoNewsComment);

        List<AddEcoNewsCommentDtoRequest> expected = List.of(
                ModelUtils.getAddEcoNewsCommentDtoRequest(),
                ModelUtils.getAddEcoNewsCommentDtoRequest()
        );
        // Act
        List<AddEcoNewsCommentDtoRequest> actual = UtilsMapper.mapAllToList(ecoNewsCommentList, AddEcoNewsCommentDtoRequest.class);

        long expectedSize = expected.size();
        long actualSize = actual.size();

        String expectedText = expected.getFirst().getText();
        String actualText = actual.getFirst().getText();

        // Assert
        assertEquals(expectedSize, actualSize);
        assertEquals(expectedText, actualText);
    }

    @Test
    void mapAllToSet() {
        // Arrange
        List<EcoNewsComment> ecoNewsCommentList = List.of(this.ecoNewsComment, this.ecoNewsComment);

        List<AddEcoNewsCommentDtoRequest> expected = List.of(
                ModelUtils.getAddEcoNewsCommentDtoRequest(),
                ModelUtils.getAddEcoNewsCommentDtoRequest()
        );

        // Act
        Set<AddEcoNewsCommentDtoRequest> actual = UtilsMapper.mapAllToSet(ecoNewsCommentList, AddEcoNewsCommentDtoRequest.class);

        long expectedSize = expected.size();
        String expectedText = expected.getFirst().getText();

        // Assert
        assertEquals(2, expectedSize);
        assertEquals(expectedText, actual.stream()
                .map(AddEcoNewsCommentDtoRequest::getText)
                .collect(Collectors.joining()));
    }
}