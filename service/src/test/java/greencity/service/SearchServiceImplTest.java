package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {
    @Mock
    private EcoNewsService ecoNewsService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private SearchServiceImpl searchService;

    private final String SEARCH_QUERY = "test";
    private final String LANGUAGE_CODE = "en";

    @Test
    void searchAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableDto<SearchEventDto> expected = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );

        when(this.eventService.search(any(Pageable.class), anyString(), anyString()))
                .thenReturn(expected);

        PageableDto<SearchEventDto> result = this.searchService.searchAllEvents(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );

        assertEquals(expected, result);
        verify(this.eventService).search(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );
    }

    @Test
    void searchAllEvents_withEmptyQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchQuery = "";
        PageableDto<SearchEventDto> expected = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );

        when(this.eventService.search(any(Pageable.class), anyString(), anyString()))
                .thenReturn(expected);

        PageableDto<SearchEventDto> result = this.searchService.searchAllEvents(
                pageable,
                searchQuery,
                this.LANGUAGE_CODE
        );

        assertEquals(expected, result);
        verify(this.eventService).search(
                pageable,
                searchQuery,
                this.LANGUAGE_CODE
        );
    }

    @Test
    void searchAllEvents_withInvalidLanguageCode() {
        Pageable pageable = PageRequest.of(0, 10);
        String languageCode = "invalid";
        PageableDto<SearchEventDto> expected = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );

        when(this.eventService.search(any(Pageable.class), anyString(), anyString()))
                .thenReturn(expected);

        PageableDto<SearchEventDto> result = this.searchService.searchAllEvents(
                pageable,
                this.SEARCH_QUERY,
                languageCode
        );

        assertEquals(expected, result);
        verify(this.eventService).search(
                pageable,
                this.SEARCH_QUERY,
                languageCode
        );
    }

    @Test
    void search() {
        PageableDto<SearchNewsDto> ecoNews = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );
        SearchResponseDto expected = SearchResponseDto.builder()
                .ecoNews(ecoNews.getPage())
                .countOfResults(ecoNews.getTotalElements())
                .build();

        when(this.ecoNewsService.search(anyString(), anyString()))
                .thenReturn(ecoNews);

        SearchResponseDto result = this.searchService.search(
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );

        assertEquals(expected, result);
        verify(this.ecoNewsService).search(this.SEARCH_QUERY, this.LANGUAGE_CODE);
    }

    @Test
    void searchAllNews() {
        Pageable pageable = PageRequest.of(0, 10);
        PageableDto<SearchNewsDto> expected = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );

        when(this.ecoNewsService.search(any(Pageable.class), anyString(), anyString()))
                .thenReturn(expected);

        PageableDto<SearchNewsDto> result = this.searchService.searchAllNews(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );

        assertEquals(expected, result);
        verify(this.ecoNewsService).search(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );
    }
    @Test
    void searchAllEvents_withDifferentPageNumbers() {
        Pageable pageable = PageRequest.of(1, 10);
        PageableDto<SearchEventDto> expected = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );

        when(this.eventService.search(any(Pageable.class), anyString(), anyString()))
                .thenReturn(expected);

        PageableDto<SearchEventDto> result = this.searchService.searchAllEvents(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );

        assertEquals(expected, result);
        verify(this.eventService).search(
                pageable,
               this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );
    }

    @Test
    void searchAllNews_withDifferentPageNumbers() {
        Pageable pageable = PageRequest.of(2, 10);
        PageableDto<SearchNewsDto> expected = new PageableDto<>(
                Collections.emptyList(),
                0,
                0,
                0
        );

        when(this.ecoNewsService.search(any(Pageable.class), anyString(), anyString()))
                .thenReturn(expected);

        PageableDto<SearchNewsDto> result = this.searchService.searchAllNews(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );

        assertEquals(expected, result);
        verify(this.ecoNewsService).search(
                pageable,
                this.SEARCH_QUERY,
                this.LANGUAGE_CODE
        );
    }
}