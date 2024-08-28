package greencity.dto.newslettersubscriber;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NewsletterSubscriberDto {

    private Long id;

    @Email
    @NotEmpty
    private String email;
}
