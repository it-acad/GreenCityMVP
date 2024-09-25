package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
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
}
