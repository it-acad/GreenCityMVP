package greencity.service;

import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;

import java.util.List;

public interface EventService {

      /**
       * Method saving new event.
       *
       * @param eventCreationDtoRequest The dto for creating new event {@link EventCreationDtoRequest}
       * @param images list of {@link MultipartFile}
       * @param userEmail Represents user email.
       * @return set of {@link EventDto} instances which belong to user.
       */
      EventDto saveEvent(EventCreationDtoRequest eventCreationDtoRequest, List<MultipartFile> images, String userEmail);

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
