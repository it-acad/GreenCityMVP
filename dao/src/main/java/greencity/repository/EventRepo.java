package greencity.repository;

import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /**
     * Method to find set of {@link Event} by author id.
     *
     * @param authorId {@link greencity.entity.User} id.
     * @return set of {@link Event} instance.
     */
    Set<Event> findAllByAuthorId(Long authorId);

}
