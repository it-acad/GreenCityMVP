package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static greencity.constant.SearchConstants.*;

@Repository
@RequiredArgsConstructor
public class EventSearchRepo {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    /**
     * Finds events based on the given pageable, search query, and language code.
     *
     * @param pageable the pagination information
     * @param searchQuery the search query string
     * @param languageCode the language code for filtering
     * @return a page of events matching the search criteria
     */
    public Page<Event> find(Pageable pageable, String searchQuery, String languageCode) {
        CriteriaQuery<Event> criteriaQuery = this.criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        searchQuery = searchQuery
                .trim()
                .replace(UNDERSCORE, ESCAPED_UNDERSCORE)
                .replace(PERCENT, ESCAPED_PERCENT)
                .replace(BACKSLASH, ESCAPED_BACKSLASH);

        Predicate finalPredicate = createFinalPredicate(
                criteriaQuery,
                root,
                searchQuery,
                languageCode
        );
        List<Order> orderList = createOrderListFromPageable(pageable, root);

        criteriaQuery.select(root)
                .distinct(true)
                .where(finalPredicate)
                .orderBy(orderList);

        TypedQuery<Event> typedQuery = this.entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Event> resultList = typedQuery.getResultList();

        long count = getCount(finalPredicate);

        return new PageImpl<>(resultList, pageable, count);
    }

    /**
     * Creates the final predicate for the search query.
     *
     * @param criteriaQuery the criteria query
     * @param root the root of the query
     * @param searchQuery the search query string
     * @param languageCode the language code for filtering
     * @return the final predicate combining all search conditions
     */
    private Predicate createFinalPredicate(CriteriaQuery<Event> criteriaQuery, Root<Event> root,
                                           String searchQuery, String languageCode) {
        List<Predicate> predicateList = createTitleAndDescriptionPredicates(root, searchQuery);
        predicateList.add(createTagSubqueryPredicate(
                criteriaQuery,
                root,
                searchQuery,
                languageCode)
        );
        return this.criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }

    /**
     * Creates predicates for searching by title and description.
     *
     * @param root the root of the query
     * @param searchQuery the search query string
     * @return a list of predicates for title and description search
     */
    private List<Predicate> createTitleAndDescriptionPredicates(Root<Event> root, String searchQuery) {
        List<Predicate> predicateList = new ArrayList<>();
        Expression<String> title = root.get(GET_TITLE).as(String.class);
        Expression<String> text = root.get(GET_DESCRIPTION).as(String.class);
        Arrays.stream(searchQuery.split(EMPTY_STRING_SPLITTER))
                .forEach(partOfSearchingText -> predicateList.add(
                this.criteriaBuilder.or(
                        this.criteriaBuilder.like(this.criteriaBuilder.lower(title), "%"
                                + partOfSearchingText.toLowerCase() + "%"),
                        this.criteriaBuilder.like(this.criteriaBuilder.lower(text), "%"
                                + partOfSearchingText.toLowerCase() + "%"))));
        return predicateList;
    }

    /**
     * Creates a subquery predicate for searching by tags.
     *
     * @param criteriaQuery the criteria query
     * @param root the root of the query
     * @param searchQuery the search query string
     * @param languageCode the language code for filtering
     * @return a predicate for tag search
     */
    private Predicate createTagSubqueryPredicate(CriteriaQuery<Event> criteriaQuery, Root<Event> root, String searchQuery, String languageCode) {
        Subquery<Tag> tagSubquery = criteriaQuery.subquery(Tag.class);
        Root<Tag> tagRoot = tagSubquery.from(Tag.class);
        Join<Event, Tag> ecoNewsTagJoin = tagRoot.join(JOIN_EVENTS);

        Subquery<TagTranslation> tagTranslationSubquery = criteriaQuery.subquery(TagTranslation.class);
        Root<Tag> tagTranslationRoot = tagTranslationSubquery.correlate(tagRoot);
        Join<TagTranslation, Tag> tagTranslationTagJoin = tagTranslationRoot.join(JOIN_TAG_TRANSLATIONS);

        List<Predicate> tagPredicateList = createTagPredicateList(
                searchQuery,
                languageCode,
                tagTranslationTagJoin
        );

        Predicate tagPredicate = this.criteriaBuilder.or(tagPredicateList.toArray(new Predicate[0]));

        tagTranslationSubquery.select(tagTranslationTagJoin.get(GET_NAME)).where(tagPredicate);

        tagSubquery.select(ecoNewsTagJoin).where(this.criteriaBuilder.exists(tagTranslationSubquery));

        return this.criteriaBuilder.in(root.get(GET_ID)).value(tagSubquery);
    }


    /**
     * Creates a list of order conditions based on the pageable object.
     *
     * @param pageable the pagination information
     * @param root the root of the query
     * @return a list of order conditions
     */
    private List<Order> createOrderListFromPageable(Pageable pageable, Root<Event> root) {
        List<Order> orderList = new ArrayList<>();
        pageable.getSort().get().forEach(order -> {
            if (order.getProperty().equalsIgnoreCase(IGNORE_RELEVANCE)) {
                orderList.add(this.criteriaBuilder.desc(this.criteriaBuilder.size(
                        root.get(GET_USER_LIKED_EVENTS))));
            } else {
                if (order.isAscending()) {
                    orderList.add(this.criteriaBuilder.asc(root.get(order.getProperty())));
                } else {
                    orderList.add(this.criteriaBuilder.desc(root.get(order.getProperty())));
                }
            }
        });
        return orderList;
    }

    /**
     * Gets the count of events matching the final predicate.
     *
     * @param finalPredicate the final predicate combining all search conditions
     * @return the count of matching events
     */
    private long getCount(Predicate finalPredicate) {
        CriteriaQuery<Long> countQuery = this.criteriaBuilder.createQuery(Long.class);
        Root<Event> countEcoNewsRoot = countQuery.from(Event.class);
        countQuery.select(this.criteriaBuilder.count(countEcoNewsRoot)).where(finalPredicate);
        return this.entityManager.createQuery(countQuery).getSingleResult();
    }

    /**
     * Creates a list of predicates for searching by tag translations.
     *
     * @param searchQuery the search query string
     * @param languageCode the language code for filtering
     * @param tagTranslationTagJoin the join object for tag translations
     * @return a list of predicates for tag translation search
     */
    private List<Predicate> createTagPredicateList(String searchQuery, String languageCode,
                                                   Join<TagTranslation, Tag> tagTranslationTagJoin) {
        List<Predicate> tagPredicateList = new ArrayList<>();
        Arrays.stream(searchQuery.split(EMPTY_STRING_SPLITTER))
                .forEach(partOfSearchingText ->
                        tagPredicateList.add(createTagPredicate(
                                partOfSearchingText,
                                languageCode,
                                tagTranslationTagJoin)));
        return tagPredicateList;
    }

    /**
     * Creates a predicate for a single tag translation search condition.
     *
     * @param partOfSearchingText a part of the search query string
     * @param languageCode the language code for filtering
     * @param tagTranslationTagJoin the join object for tag translations
     * @return a predicate for a single tag translation search condition
     */
    private Predicate createTagPredicate(String partOfSearchingText, String languageCode,
                                         Join<TagTranslation, Tag> tagTranslationTagJoin) {
        return this.criteriaBuilder.and(
                this.criteriaBuilder.like(this.criteriaBuilder.lower(tagTranslationTagJoin.get(GET_NAME)),
                        "%" + partOfSearchingText.toLowerCase() + "%"),
                this.criteriaBuilder.like(this.criteriaBuilder
                                .lower(tagTranslationTagJoin.get(GET_LANGUAGE)
                                        .get(GET_CODE)),
                        "%" + languageCode.toLowerCase() + "%"));
    }
}