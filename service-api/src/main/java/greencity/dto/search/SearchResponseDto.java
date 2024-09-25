package greencity.dto.search;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SearchResponseDto {
    private List<SearchNewsDto> ecoNews;
    private Long countOfResults;
}
