package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.friends.FriendCardDtoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Provides the interface to manage search functionality.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface SearchService {
    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return {@link SearchResponseDto}
     */
    SearchResponseDto search(String searchQuery, String languageCode);

    /**
     * Method that allow you to search {@link SearchNewsDto}.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     */
    PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method that allow you to search users which are not your friend yet by login or name.
     * and get result as list of{@link FriendCardDtoResponse}.
     *
     * @param userId The ID of the current user.
     * @param searchQuery The search query pattern for login and name field.
     * @param city The search query pattern for city field.
     * @return list of {@link FriendCardDtoResponse} instances.
     *
     * @author Chernenko Vitaliy
     */
    List<FriendCardDtoResponse> searchFriends(long userId, String searchQuery, String city);
}
