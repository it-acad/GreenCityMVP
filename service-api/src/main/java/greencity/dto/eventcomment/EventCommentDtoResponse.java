package greencity.dto.eventcomment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCommentDtoResponse {
    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    private EventCommentAuthorDto author;

    @NotEmpty
    private String text;

    @CreatedDate
    private LocalDateTime createdDate;
}
