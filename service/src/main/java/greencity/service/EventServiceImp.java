package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.entity.EventImage;
import greencity.repository.EventDayDetailsRepo;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import greencity.exception.exceptions.EventNotFoundException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImp implements EventService {

    private final EventRepo eventRepo;
    private final FileService fileService;
    private final ModelMapper modelMapper;
    private final EventDayDetailsRepo eventDayDetailsRepo;


    @Override
    public EventDto save(final MultipartFile[] images, final EventCreationDto eventCreationDto, final Long userId) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or @eventServiceImp.isEventOwner(#eventId, #userId)")
    @Transactional
    public void delete(Long eventId, Long userId) {
        if (eventRepo.existsById(eventId)) {
            eventRepo.deleteById(eventId);
        } else {
            log.error("Event with id {} does not exist to delete", eventId);
            throw new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or @eventServiceImp.isEventOwner(#eventId, #userId)")
    @Transactional
    public EventDto update(EventEditDto eventEditDto, Long userId, Long eventId, MultipartFile[] images) {

        Optional<Event> optionalCurrentEvent = eventRepo.findById(eventId);
        if (optionalCurrentEvent.isEmpty()) {
            log.error("Event with id {} does not exist to update", eventId);
            throw new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventId);
        } else {

            Event currentEvent = optionalCurrentEvent.get();
            Event updatedEvent = modelMapper.map(eventEditDto, Event.class);

            currentEvent.setEventTitle(updatedEvent.getEventTitle());
            currentEvent.setEventType(updatedEvent.getEventType());
            currentEvent.setDescription(updatedEvent.getDescription());
            currentEvent.getEventDayDetailsList().clear();
            for (EventDayDetails day : updatedEvent.getEventDayDetailsList()) {
                currentEvent.addEventDayDetails(day);
            }
            saveOrUpdateEventDayDetails(updatedEvent.getEventDayDetailsList());
            currentEvent.setImages(updatedEvent.getImages());

            if (images != null) {
                for (MultipartFile file : images) {
                    String imagePath = fileService.upload(file);
                    updatedEvent.getImages().add(EventImage.builder().imagePath(imagePath).build());
                }
            }
            eventRepo.saveAndFlush(currentEvent);
            return modelMapper.map(currentEvent, EventDto.class);
        }
    }

    public boolean isEventOwner(Long eventId, Long userId) {
        return eventRepo.findById(eventId)
                .map(it -> it.getAuthor().getId().equals(userId))
                .orElse(false);
    }

    public void saveOrUpdateEventDayDetails(Set<EventDayDetails> setEventDayDetails) {
        for (EventDayDetails details : setEventDayDetails) {
            if (details.getId() == null) {
                eventDayDetailsRepo.saveAndFlush(details);
            } else {
                EventDayDetails eventDayDetails = eventDayDetailsRepo.findById(details.getId()).get();
                if (details.getEventDate() != null) {
                    eventDayDetails.setEventDate(details.getEventDate());
                }
                if (details.getEventStartTime() != null) {
                    eventDayDetails.setEventStartTime(details.getEventStartTime());
                }
                if (details.getEventEndTime() != null) {
                    eventDayDetails.setEventEndTime(details.getEventEndTime());
                }
                if (details.getOfflinePlace() != null) {
                    eventDayDetails.setOfflinePlace(details.getOfflinePlace());
                }
                if (details.getOnlinePlace() != null) {
                    eventDayDetails.setOnlinePlace(details.getOnlinePlace());
                }
                eventDayDetails.setAllDateDuration(details.isAllDateDuration());
                eventDayDetails.setOnline(details.isOnline());
                eventDayDetails.setOffline(details.isOffline());
            }
        }
    }
}