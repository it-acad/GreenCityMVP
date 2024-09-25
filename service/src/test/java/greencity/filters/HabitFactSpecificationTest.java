package greencity.filters;

import greencity.dto.habitfact.HabitFactViewDto;
import greencity.entity.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HabitFactSpecificationTest {
    @Mock
    private Root<HabitFact> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Long> pathLong;

    @Mock
    private Path<HabitFact> habitFactPath;

    @Mock
    private Predicate predicate;

    @Mock
    private Root<HabitFactTranslation> habitFactTranslationRoot;

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private CriteriaQuery criteriaQueryMock;

    @Mock
    private Root<HabitFact> habitFactRootMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Predicate andIdPredicate;

    @Mock
    private Path<Object> pathHabitFactIdMock;

    @Mock
    private SingularAttribute<HabitFact, Habit> habit;

    @Mock
    private SingularAttribute<HabitFact, Long> habitFactId;

    @Mock
    private SingularAttribute<Habit, Long> habitId;

    @Mock
    private SingularAttribute<HabitFactTranslation, HabitFact> habitFact;

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
                .key(HabitFact_.HABIT)
                .type(HabitFact_.HABIT)
                .value(habitFactViewDto.getHabitId())
                .build());
        criteriaList.add(SearchCriteria.builder()
                .key(HabitFact_.TRANSLATIONS)
                .type(HabitFact_.TRANSLATIONS)
                .value(habitFactViewDto.getContent())
                .build());

        HabitFact_.id = habitFactId;
        HabitFact_.habit = habit;
        HabitFactTranslation_.habitFact = habitFact;
        Habit_.id = habitId;

        habitFactSpecification = new HabitFactSpecification(criteriaList);
    }

    @Test
    void toPredicate_id() {
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        when(habitFactRootMock.get(HabitFact_.ID)).thenReturn(pathHabitFactIdMock);
        when(criteriaBuilderMock.equal(pathHabitFactIdMock, criteriaList.get(0).getValue().toString()))
                .thenReturn(andIdPredicate);
        when(criteriaBuilderMock.and(predicateMock, andIdPredicate)).thenReturn(andIdPredicate);

        Predicate result = habitFactSpecification.toPredicate(habitFactRootMock, criteriaQueryMock, criteriaBuilderMock);

        assertNotNull(result, "Predicate shouldn't be null");
        verify(criteriaBuilderMock).equal(pathHabitFactIdMock, "1");
        verify(criteriaBuilderMock).and(predicateMock, andIdPredicate);
        assertSame(andIdPredicate, result);
    }

    private HabitFactSpecification createSpecification(String key, String type) {
        SearchCriteria searchCriteria = new SearchCriteria(key, "1", type);
        List<SearchCriteria> criteriaList = List.of(searchCriteria);
        return new HabitFactSpecification(criteriaList);
    }

    @Test
    void toPredicate_habitId(){
        HabitFactSpecification specification = createSpecification("1", "habitId");

        Join<HabitFact, Habit> habitJoin = mock(Join.class);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), "1")).thenReturn(predicate);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(predicate, predicate)).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result, "Predicate shouldn't be null");
        verify(criteriaBuilder).equal(habitJoin.get(Habit_.id), "1");
    }

    @Test
    void toPredicate_content(){
        HabitFactSpecification specification = createSpecification("test", "content");

        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        Path<String> contentPath = mock(Path.class);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.content)).thenReturn(contentPath);

        when(criteriaBuilder.like(contentPath, "%test%")).thenReturn(predicate);
        when(root.get(HabitFact_.id)).thenReturn(pathLong);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(habitFactPath);
        when(habitFactPath.get(HabitFact_.id)).thenReturn(pathLong);
        when(criteriaBuilder.equal(pathLong, pathLong)).thenReturn(predicate);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(predicate, predicate)).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertNotNull(result, "Predicate should not be null");
        verify(criteriaBuilder).like(contentPath, "%test%");
        verify(criteriaBuilder).equal(pathLong, pathLong);
    }

    @Test
    void toPredicate_withEmptyCriteriaList() {
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        habitFactSpecification = new HabitFactSpecification(new ArrayList<>());
        Predicate result = habitFactSpecification.toPredicate(habitFactRootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(criteriaBuilderMock).conjunction();
        assertSame(predicateMock, result);
    }
}
