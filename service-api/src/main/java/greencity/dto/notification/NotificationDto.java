package greencity.dto.notification;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationDto {
    private Long id;
    private String section;
    private String text;
    private boolean isRead;
    private String receivedTime;
    private Long userId;
}
