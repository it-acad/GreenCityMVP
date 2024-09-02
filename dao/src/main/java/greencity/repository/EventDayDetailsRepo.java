package greencity.repository;

import greencity.entity.EventDayDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDayDetailsRepo extends JpaRepository<EventDayDetails, Long> {
}
