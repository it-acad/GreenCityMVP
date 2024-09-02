package greencity.repository;

import greencity.entity.EventDayDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDayDetailsRepo extends JpaRepository<EventDayDetails, Long> {
}
