package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.entity.User;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final EventService eventService;

    /**
     * Searches for events using the provided query and language code, and returns the results in a paginated format.
     * Delegates the search to {@link EventService} and returns a {@link PageableDto} with event details and pagination.
     *
     * @param pageable       pagination and sorting information
     * @param searchQuery    query string for filtering events
     * @param languageCode   code for filtering by tag translations
     * @return a {@link PageableDto} containing a list of {@link SearchEventDto} and pagination details
     */
    @Override
    public PageableDto<SearchEventDto> searchAllEvents(Pageable pageable, String searchQuery, String languageCode) {
        return this.eventService.search(pageable, searchQuery, languageCode);
    }

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery, String languageCode) {
        PageableDto<SearchNewsDto> ecoNews = this.ecoNewsService.search(searchQuery, languageCode);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .countOfResults(ecoNews.getTotalElements())
            .build();
    }

    /**
     * Searches for news articles based on the given query and returns the results in a pageable format.
     * Delegates the search logic to the {@link EcoNewsService}.
     *
     * @param pageable      the pagination and sorting information
     * @param searchQuery   the query text used to search news
     * @param languageCode  the language code to filter news by language
     * @return a {@link PageableDto} containing the search results in the form of {@link SearchNewsDto}
     */
    @Override
    public PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode) {
        return this.ecoNewsService.search(pageable, searchQuery, languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<FriendCardDtoResponse> searchNotFriendsYet(long userId, String searchQuery, String city, Pageable page) {
        Page<User> notFriendsUsers;

        if (city != null && !city.isEmpty()) {
             notFriendsUsers = userRepo.getAllUsersByNameAndCityExceptMainUserAndFriends(userId, searchQuery.toLowerCase(), city.toLowerCase(), page);
        } else {
            notFriendsUsers = userRepo.getAllUsersByNameExceptMainUserAndFriends(userId, searchQuery.toLowerCase(), page);
        }

        return buildPageableAdvancedGeneticDto(notFriendsUsers, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<FriendCardDtoResponse> searchFriends(long userId, Pageable page) {
        Page<User> friends = userRepo.getAllUsersFriends(userId, page);

        return buildPageableAdvancedGeneticDto(friends, userId);
    }

    private PageableAdvancedDto<FriendCardDtoResponse> buildPageableAdvancedGeneticDto(Page<User> usersPage, long userId) {
        List<FriendCardDtoResponse> friendsDto = usersPage.stream()
                .map(user -> modelMapper.map(user, FriendCardDtoResponse.class))
                .toList();

        friendsDto.forEach(friend -> friend.setMutualFriends(userRepo.getAmountOfMutualFriends(userId, friend.getId())));


        return new PageableAdvancedDto<>(
                friendsDto,
                usersPage.getTotalElements(),
                usersPage.getPageable().getPageNumber(),
                usersPage.getTotalPages(),
                usersPage.getNumber(),
                usersPage.hasPrevious(),
                usersPage.hasNext(),
                usersPage.isFirst(),
                usersPage.isLast());
    }
}
