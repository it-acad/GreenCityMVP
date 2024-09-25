package greencity.entity;

import greencity.enums.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude ={"images", "eventDayDetailsList", "participants"})
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

    @OneToMany(mappedBy="event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventDayDetails> eventDayDetailsList = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "events_event_images",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<EventImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventParticipant> participants = new HashSet<>();

    public void addEventDayDetails(EventDayDetails eventDayDetails){
        this.eventDayDetailsList.add(eventDayDetails);
        eventDayDetails.setEvent(this);
    }
}
