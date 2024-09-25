package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddEventCommentDtoResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private String userName;
    private String text;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
