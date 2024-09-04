package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.entity.User;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery, String languageCode) {
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery, languageCode);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .countOfResults(ecoNews.getTotalElements())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode) {
        return ecoNewsService.search(pageable, searchQuery, languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FriendCardDtoResponse> searchFriends(long userId, String searchQuery, String city) {
        List<User> notFriendsUsers;
        if (city != null && !city.isEmpty()) {
             notFriendsUsers = userRepo.getAllUsersByNameAndCityExceptMainUserAndFriends(userId, searchQuery.toLowerCase(), city.toLowerCase());
        } else {
            notFriendsUsers = userRepo.getAllUsersByNameExceptMainUserAndFriends(userId, searchQuery.toLowerCase());
        }

        List<FriendCardDtoResponse> result = notFriendsUsers.stream().map(user -> modelMapper.map(user, FriendCardDtoResponse.class))
                .collect(Collectors.toList());

        result.forEach(friend -> friend.setMutualFriends(userRepo.getAmountOfMutualFriends(userId, friend.getId())));
        return result;
    }
}
