package greencity.dto.replytocomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyToCommentRequestDto {
    @NotBlank
    @NonNull
    @Size(min = 1, max = 8000)
    private String content;
}
