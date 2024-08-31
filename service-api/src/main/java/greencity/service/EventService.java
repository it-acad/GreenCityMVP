package greencity.service;

import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;

public interface EventService {

      EventDto save(MultipartFile[] images, EventCreationDto event, Long userId);

      /**
       * Method for getting user's events by user id.
       *
       * @param userId user id.
       * @return set of {@link EventDto} instances which belong to user.
       */
      Set<EventDto> findAllByUserId(Long userId);

      /**
       * Method for getting all events.
       *
       * @return set of {@link EventDto} instances.
       */
      Set<EventDto> findAll();
}
