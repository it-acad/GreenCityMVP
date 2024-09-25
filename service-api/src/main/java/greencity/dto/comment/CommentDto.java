package greencity.dto.comment;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @Size(min = 1, max = 8000)
    private String text;
}
