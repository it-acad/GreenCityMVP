package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.EventVO;

public interface EventService {

      EventVO save(EventDto event);
}
