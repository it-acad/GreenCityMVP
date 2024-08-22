package greencity.entity;

import greencity.enums.EventType;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(nullable = false)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType = EventType.OPEN;

    @OneToMany(mappedBy="event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventDayDetails> durationAndLocationList;

    @ManyToMany
    @JoinTable(
            name = "events_event_images",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "event_image_id"))
    private Set<EventImage> images;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
}
