package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String section; // maybe enum

    @Column(nullable = false)
    private String text;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "received_time")
    private LocalDateTime receivedTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // add to user entity
    private User user;
}
