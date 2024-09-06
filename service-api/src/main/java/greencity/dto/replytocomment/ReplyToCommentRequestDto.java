package greencity.dto.replytocomment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyToCommentRequestDto {
    private Long id;
    private String content;
    private Boolean isEdited;
}
