package greencity.repository;

import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface EventRepo extends JpaRepository<Event, Long> {

    /**
     * Method to find set of {@link Event} by author id.
     *
     * @param authorId {@link greencity.entity.User} id.
     * @return set of {@link Event} instance.
     */
    Set<Event> findAllByAuthorId(Long authorId);

}
