package greencity.service;

import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

      EventDto save(MultipartFile[] images, EventCreationDto event, Long userId);
      void delete(Long eventId, Long userId);
      EventDto update(EventEditDto event, Long userId, Long eventId, MultipartFile[] images);
}
