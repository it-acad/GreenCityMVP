package greencity.filters;

import greencity.ModelUtils;
import greencity.annotations.RatingCalculationEnum;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.RatingStatistics;
import greencity.entity.RatingStatistics_;
import greencity.entity.User;
import greencity.entity.User_;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RatingStatisticsSpecificationTest {

    private List<SearchCriteria> searchCriteriaList;
    private RatingStatisticsSpecification ratingStatisticsSpecification;

    @Mock
    private Root<RatingStatistics> root;

    @Mock
    private Predicate expectedPredicate;

    @Mock
    private CriteriaQuery<RatingStatistics> criteriaQuery;

    @Mock
    private Path<Object> objectPath;

    @Mock
    private Join<RatingStatistics, User> userJoin;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    private static final String ID_CRITERIA = "id";
    private static final String ENUM_CRITERIA = "enum";
    private static final String USER_ID_CRITERIA = "userId";
    private static final String USER_MAIL_CRITERIA = "userMail";
    private static final String DATE_RANGE_CRITERIA = "dateRange";
    private static final String POINTS_CHANGED_CRITERIA = "pointsChanged";
    private static final String CURRENT_RATING_CRITERIA = "currentRating";

    /**
     * Set up the test environment
     */
    @BeforeEach
    void setUp() {
        RatingStatisticsViewDto dto = ModelUtils.getRatingStatisticsViewDto();
        searchCriteriaList = List.of(
                createCriteria(RatingStatistics_.ID, ID_CRITERIA, dto.getId()),
                createCriteria(RatingStatistics_.RATING_CALCULATION_ENUM, ENUM_CRITERIA, dto.getEventName()),
                createCriteria(RatingStatistics_.USER, USER_ID_CRITERIA, dto.getUserId()),
                createCriteria(RatingStatistics_.USER, USER_MAIL_CRITERIA, dto.getUserEmail()),
                createCriteria(RatingStatistics_.CREATE_DATE, DATE_RANGE_CRITERIA, new String[]{dto.getStartDate(), dto.getEndDate()}),
                createCriteria(RatingStatistics_.POINTS_CHANGED, POINTS_CHANGED_CRITERIA, dto.getPointsChanged()),
                createCriteria(RatingStatistics_.RATING, CURRENT_RATING_CRITERIA, dto.getCurrentRating())
        );
        ratingStatisticsSpecification = new RatingStatisticsSpecification(searchCriteriaList);
    }

    /**
     * Test: null criteria list
     */
    @Test
    void toPredicate_NullCriteriaList_ThrowsNullPointerException() {
        ratingStatisticsSpecification = new RatingStatisticsSpecification(null);

        when(criteriaBuilder.conjunction()).thenReturn(expectedPredicate);

        assertThatThrownBy(() -> ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder))
                .isInstanceOf(NullPointerException.class);

        verify(criteriaBuilder).conjunction();
    }

    /**
     * Test: null type
     */
    @Test
    void toPredicate_NullType_ThrowsNullPointerException() {
        SearchCriteria criteria = searchCriteriaList.getFirst();
        criteria.setType(null);

        when(criteriaBuilder.conjunction()).thenReturn(expectedPredicate);

        assertThatThrownBy(() -> ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder))
                .isInstanceOf(NullPointerException.class);

        verify(criteriaBuilder).conjunction();
    }

    /**
     * Test: valid data
     */
    @Test
    void toPredicate_ValidData_ReturnsPredicate() {
        when(criteriaBuilder.conjunction()).thenReturn(expectedPredicate);

        configureMocksForCriteria();

        when(criteriaBuilder.and(expectedPredicate, expectedPredicate)).thenReturn(expectedPredicate);

        Predicate actual = ratingStatisticsSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertThat(actual).isEqualTo(expectedPredicate);

        verify(criteriaBuilder).conjunction();
        verify(criteriaBuilder).disjunction();
        verify(criteriaBuilder, times(searchCriteriaList.size())).and(expectedPredicate, expectedPredicate);
        verify(root, times(5)).get(anyString());
    }

    /**
     * Configure mocks for each criteria type
     */
    private void configureMocksForCriteria() {
        searchCriteriaList.forEach(criteria -> {
            Consumer<SearchCriteria> mockAction = getMockActionForCriteriaType(criteria.getType());
            if (mockAction != null) {
                mockAction.accept(criteria);
            }
        });
    }

    /**
     * Get the mock action based on the criteria type
     */
    private Consumer<SearchCriteria> getMockActionForCriteriaType(String type) {
        return switch (type) {
            case ID_CRITERIA, POINTS_CHANGED_CRITERIA, CURRENT_RATING_CRITERIA -> this::mockNumericPredicate;
            case ENUM_CRITERIA -> this::mockEnumPredicate;
            case USER_ID_CRITERIA -> this::mockUserIdPredicate;
            case USER_MAIL_CRITERIA -> this::mockUserMailPredicate;
            case DATE_RANGE_CRITERIA -> this::mockDateRangePredicate;
            default -> throw new IllegalArgumentException("Unknown criteria type: " + type);
        };
    }

    /**
     * Mock numeric predicate
     */
    private void mockNumericPredicate(SearchCriteria criteria) {
        when(root.get(criteria.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, criteria.getValue())).thenReturn(expectedPredicate);
    }

    /**
     * Mock enum predicate
     */
    private void mockEnumPredicate(SearchCriteria criteria) {
        when(criteriaBuilder.disjunction()).thenReturn(expectedPredicate);
        when(root.get(criteria.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.equal(objectPath, RatingCalculationEnum.valueOf((String) criteria.getValue()))).thenReturn(expectedPredicate);
        when(criteriaBuilder.or(expectedPredicate, expectedPredicate)).thenReturn(expectedPredicate);
    }

    /**
     * Mock user ID predicate
     */
    private void mockUserIdPredicate(SearchCriteria criteria) {
        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);
        when(userJoin.get(User_.id)).thenReturn((Path) objectPath);
        when(criteriaBuilder.equal(objectPath, criteria.getValue())).thenReturn(expectedPredicate);
    }

    /**
     * Mock user mail predicate
     */
    private void mockUserMailPredicate(SearchCriteria criteria) {
        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);
        when(userJoin.get(User_.email)).thenReturn((Path) objectPath);
        when(criteriaBuilder.like(any(), eq("%" + criteria.getValue() + "%"))).thenReturn(expectedPredicate);
    }

    /**
     * Mock date range predicate
     */
    private void mockDateRangePredicate(SearchCriteria criteria) {
        String[] dates = (String[]) criteria.getValue();
        var start = LocalDate.parse(dates[0]).atStartOfDay(ZoneOffset.UTC);
        var end = LocalDate.parse(dates[1]).atStartOfDay(ZoneOffset.UTC);

        when(root.get(criteria.getKey())).thenReturn(objectPath);
        when(criteriaBuilder.between((Path) objectPath, start, end)).thenReturn(expectedPredicate);
    }

    /**
     * Create a SearchCriteria instance
     */
    private SearchCriteria createCriteria(String key, String type, Object value) {
        return SearchCriteria.builder()
                .key(key)
                .type(type)
                .value(value)
                .build();
    }


}