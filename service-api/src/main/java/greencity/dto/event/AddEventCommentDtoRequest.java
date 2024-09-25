package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddEventCommentDtoRequest {

    @NotBlank(message = "Text couldn't be longer then 8000 characters")
    @Size(max = 8000)
    @Pattern(regexp = "^[a-zA-Z0-9!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~\\s]+$",
            message = "Comment contains invalid characters.")
    private String text;

    private Long parentCommentId;
}
