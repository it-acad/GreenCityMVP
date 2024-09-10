package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.event.EventSendEmailDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.entity.EventImage;
import greencity.entity.User;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventDayDetailsRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static greencity.constant.AppConstant.DEFAULT_EVENT_IMAGE;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final FileService fileService;
    private final UserService userService;
    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;
    private final ModelMapper modelMapper;
    private final EventDayDetailsRepo eventDayDetailsRepo;


    @Override
    @Transactional
    public EventDto saveEvent(EventCreationDtoRequest eventCreationDtoRequest,
                              List<MultipartFile> images,
                              String userEmail) {

        // Find the user by email
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));

        // Map eventCreationDtoRequest to Event entity using EventCreationDtoMapper
        Event eventToSave = modelMapper.map(eventCreationDtoRequest, Event.class);
        eventToSave.setAuthor(user);

        // Attempt to save the event
        try {
            Event savedEvent = eventRepo.save(eventToSave);

            // Handle images after event is saved
            List<EventImage> eventImages = uploadImages(images);
            savedEvent.setImages(eventImages);

            // Re-save the event with images
            eventRepo.save(savedEvent);

            // Send email
            sendEmailDto(savedEvent);

            // Convert the saved event to EventDto and return
            return modelMapper.map(savedEvent, EventDto.class);

        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENT_NOT_SAVED);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Chernenko Vitaliy.
     */
    @PreAuthorize("@eventServiceImpl.isCurrentUserId(#userId)")
    @Override
    public Set<EventDto> findAllByUserId(final Long userId) {
        Set<Event> eventsFromDb = eventRepo.findAllByAuthorId(userId);

        Set<EventDto> resultDto = eventsFromDb.stream().map(event -> modelMapper.map(event, EventDto.class)).collect(Collectors.toSet());
        return resultDto;
    }

    /**
     * {@inheritDoc}
     *
     * @author Chernenko Vitaliy.
     */
    @Override
    public Set<EventDto> findAll() {
        List<Event> eventsFromDb = eventRepo.findAll();

        Set<EventDto> resultDto = eventsFromDb.stream().map(event -> modelMapper.map(event, EventDto.class)).collect(Collectors.toSet());
        return resultDto;
    }

    private List<EventImage> uploadImages(List<MultipartFile> images) {
        // Initialize the list to store the event images
        List<EventImage> eventImages = new ArrayList<>();

        // If list of images is empty, add default image
        if (images == null || images.isEmpty()) {
            eventImages.add(new EventImage(DEFAULT_EVENT_IMAGE));
        } else {
            // Iterate through the list of images and upload each one
            for (MultipartFile image : images) {
                String imagePath = fileService.upload(image);
                eventImages.add(new EventImage(imagePath));
            }
        }

        // Return the list of uploaded event images
        return eventImages;
    }

    public void sendEmailDto(@NotNull Event savedEvent) {
        // Retrieve the access token from the HTTP request header
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);

        // Map the User entity to an AuthorDto object
        PlaceAuthorDto placeAuthorDto = modelMapper.map(savedEvent.getAuthor(), PlaceAuthorDto.class);

        // Get the list of event dates
        List<String> eventDayList = savedEvent.getEventDayDetailsList().stream()
                .map(eventDayDetail -> eventDayDetail.getEventDate().toString())
                .collect(Collectors.toList());

        // Calculate the duration in days
        int durationInDays = savedEvent.getEventDayDetailsList().size();

        Optional<EventDayDetails> firstDayDetail = savedEvent.getEventDayDetailsList().stream().findFirst();
        LocalTime eventStartTime = firstDayDetail.map(EventDayDetails::getEventStartTime).orElse(null);
        LocalTime eventEndTime = firstDayDetail.map(EventDayDetails::getEventEndTime).orElse(null);
        String onlinePlace = firstDayDetail.map(EventDayDetails::getOnlinePlace).orElse(null);
        String offlinePlace = firstDayDetail.map(EventDayDetails::getOfflinePlace).orElse(null);

        // Build the EventSendEmailDto object
        EventSendEmailDto eventSendEmailDto = EventSendEmailDto.builder()
                .eventTitle(savedEvent.getEventTitle())
                .description(savedEvent.getDescription())
                .eventType(savedEvent.getEventType().toString())
                .eventDayList(eventDayList) // Event dates list
                .durationInDays(durationInDays) // Duration in days
                .eventStartTime(eventStartTime)
                .eventEndTime(eventEndTime)
                .onlinePlace(onlinePlace)
                .offlinePlace(offlinePlace)
                .imagePath(savedEvent.getImages().stream()
                        .map(EventImage::getImagePath)
                        .collect(Collectors.toList()))
                .author(placeAuthorDto)
                .secureToken(accessToken)
                .build();

        // Send the event details
        restClient.addEvent(eventSendEmailDto);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or @eventServiceImpl.isEventOwner(#eventId, #userId)")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or @eventServiceImpl.isEventOwner(#eventId, #userId)")
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

    public boolean isCurrentUserId(long userId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO user = userService.findByEmail(email);
        return user.getId() == userId;
    }

    public void saveOrUpdateEventDayDetails(Set<EventDayDetails> setEventDayDetails) {
        for (EventDayDetails details : setEventDayDetails) {
            if (details.getId() == null) {
                eventDayDetailsRepo.saveAndFlush(details);
            } else {
                Optional<EventDayDetails> optionalEventDayDetails = eventDayDetailsRepo.findById(details.getId());
                if (optionalEventDayDetails.isEmpty()) {
                    throw new EventNotFoundException(ErrorMessage.EVENT_DAY_DETAILS_NOT_FOUND + details.getId());
                } else {
                    EventDayDetails eventDayDetails = optionalEventDayDetails.get();
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
                    eventDayDetails.setLatitude(details.getLatitude());
                    eventDayDetails.setLongitude(details.getLongitude());
                    eventDayDetails.setAllDateDuration(details.isAllDateDuration());
                    eventDayDetails.setOnline(details.isOnline());
                    eventDayDetails.setOffline(details.isOffline());
                    eventDayDetailsRepo.save(eventDayDetails);
                }
            }
        }
    }

}