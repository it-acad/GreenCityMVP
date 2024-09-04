package greencity.dto.replytocomment;

import jakarta.validation.constraints.NotBlank;
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
    private String content;

    private LocalDateTime createdDate;
    private Boolean isEdited;
}
