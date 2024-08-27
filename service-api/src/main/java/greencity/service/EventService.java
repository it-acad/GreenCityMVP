package greencity.service;

import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    EventDto saveEvent(EventCreationDtoRequest eventCreationDtoRequest, List<MultipartFile> images, String userEmail);
}
