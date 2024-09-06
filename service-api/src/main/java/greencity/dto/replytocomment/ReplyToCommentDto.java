package greencity.dto.replytocomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReplyToCommentDto {
    private Long id;

    @NotBlank
    @NonNull
    @Size(min = 1, max = 8000)
    private String content;

    private LocalDateTime createdDate;
    private Boolean isEdited;
}
