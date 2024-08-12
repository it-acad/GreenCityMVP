package greencity.filters;

import greencity.dto.habitfact.HabitFactViewDto;
import greencity.entity.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitFactSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private CriteriaQuery<?> criteriaQueryMock;

    @Mock
    private Root<HabitFact> habitFactRootMock;

    @Mock
    private Root<HabitFactTranslation> habitFactTranslationRootMock;

    @Mock
    private Join<HabitFact, Habit> habitJoinMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Predicate andHabitIdPredicate;

    @Mock
    private Predicate andContentPredicate;

    @Mock
    private SingularAttribute<Translation, String> contentAttributeMock;

    @Mock
    private Path<String> pathTranslationContentMock;

    private HabitFactSpecification habitFactSpecification;

    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        HabitFactViewDto habitFactViewDto = HabitFactViewDto.builder()
                .id("1")
                .habitId("2")
                .content("some content")
                .build();

        criteriaList = new ArrayList<>();
        criteriaList.add(SearchCriteria.builder()
                .key(HabitFact_.ID)
                .type(HabitFact_.ID)
                .value(habitFactViewDto.getId())
                .build());
        criteriaList.add(SearchCriteria.builder()
                .key(Habit_.ID)
                .type(Habit_.ID)
                .value(habitFactViewDto.getHabitId())
                .build());
        criteriaList.add(SearchCriteria.builder()
                .key(HabitFactTranslation_.CONTENT)
                .type(HabitFactTranslation_.CONTENT)
                .value(habitFactViewDto.getContent())
                .build());

        HabitFact_.translations = mock(ListAttribute.class);
        HabitFactTranslation_.content = contentAttributeMock;
        HabitFactTranslation_.habitFact = mock(SingularAttribute.class);

        habitFactSpecification = new HabitFactSpecification(criteriaList);
    }

    @Test
    void toPredicate() {
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);

        when(habitFactRootMock.join(HabitFact_.habit)).thenReturn(habitJoinMock);

        when(criteriaBuilderMock.equal(habitJoinMock.get(Habit_.id), criteriaList.get(1).getValue()))
                .thenReturn(andHabitIdPredicate);

        when(criteriaBuilderMock.and(predicateMock, andHabitIdPredicate)).thenReturn(andHabitIdPredicate);

        when(criteriaQueryMock.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRootMock);

        when(habitFactTranslationRootMock.get(contentAttributeMock)).thenReturn(pathTranslationContentMock);

        when(criteriaBuilderMock.like(pathTranslationContentMock, "%" + criteriaList.get(2).getValue() + "%"))
                .thenReturn(andContentPredicate);

        when(criteriaBuilderMock.equal(habitFactTranslationRootMock.get(HabitFactTranslation_.habitFact).get(HabitFact_.id),
                habitFactRootMock.get(HabitFact_.id)))
                .thenReturn(andContentPredicate);

        when(criteriaBuilderMock.and(andHabitIdPredicate, andContentPredicate)).thenReturn(andContentPredicate);

        habitFactSpecification.toPredicate(habitFactRootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(habitFactRootMock, never()).get(HabitFact_.translations);
        verify(criteriaBuilderMock).and(predicateMock, andHabitIdPredicate);
        verify(criteriaBuilderMock).and(andHabitIdPredicate, andContentPredicate);
    }
}