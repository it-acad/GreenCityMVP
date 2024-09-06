package greencity.dto.replytocomment;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReplyToCommentResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private Boolean isEdited;
}
