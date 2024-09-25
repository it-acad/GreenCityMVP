package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.EventRole;
import greencity.exception.exceptions.*;
import greencity.filters.EventSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.*;
import greencity.dto.PageableDto;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.event.EventSendEmailDto;
import greencity.dto.search.SearchEventDto;
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
import greencity.repository.EventSearchRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final EventSearchRepo eventSearchRepo;
    private final FileService fileService;
    private final UserService userService;
    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;
    private final ModelMapper modelMapper;
    private final EventDayDetailsRepo eventDayDetailsRepo;
    private final EventParticipantRepo eventParticipantRepo;

    /**
     * {@inheritDoc}
     */
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

            // Create and save the author as a participant with the role of AUTHOR
            EventParticipant participant = EventParticipant.createParticipant(savedEvent, user.getId(), EventRole.AUTHOR);
            eventParticipantRepo.save(participant);

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
     */
    @Override
    @Transactional
    public EventParticipantDto joinEvent(Long eventId, String userEmail) {
        // Find the event by ID
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventId));

        // Find the user by email
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));

        // Check if the user is already a participant
        boolean isAlreadyParticipant = eventParticipantRepo.findByEventAndUserId(event, user.getId()).isPresent();

        if (isAlreadyParticipant) {
            throw new EventAlreadyJoinedException(ErrorMessage.USER_ALREADY_JOINED_EVENT + event.getEventTitle());
        }

        // Create and save the user as a participant
        EventParticipant eventParticipant = EventParticipant.createParticipant(event, user.getId(), EventRole.PARTICIPANT);
        eventParticipantRepo.save(eventParticipant);

        return modelMapper.map(eventParticipant, EventParticipantDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EventParticipantDto leaveEvent(Long eventId, String userEmail) {
        // Find the event by ID
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventId));

        // Find the user by email
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));

        // Check if the user is the author
        if (event.getAuthor().getId().equals(user.getId())) {
            throw new EventAuthorCannotLeaveException(ErrorMessage.AUTHOR_CANNOT_LEAVE_EVENT + event.getEventTitle());
        }

        // Find the participant to remove
        EventParticipant participant = eventParticipantRepo.findByEventAndUserId(event, user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventId));

        // Remove the participant from the event
        eventParticipantRepo.delete(participant);

        // Convert the removed participant to EventParticipantDto
        return modelMapper.map(participant, EventParticipantDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("@eventServiceImpl.isCurrentUserId(#userId)")
    @Transactional(readOnly = true)
    public List<EventParticipantDto> getEventsUserJoinedOrScheduled(Long userId) {
        // Find all events where the user is a participant or author
        List<EventParticipant> participants = eventParticipantRepo.findAllByUserId(userId);

        return participants.stream()
                .map(participant -> modelMapper.map(participant, EventParticipantDto.class))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getDistinctLocations() {
        // Return the distinct locations
        return eventDayDetailsRepo.findDistinctLocations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<EventDto> findFilteredEvents(EventFilterDto filterDto, Pageable pageable) {
        EventSpecification specification = getSpecification(filterDto);

        Page<Event> filteredEvents = eventRepo.findAll(specification, pageable);

        return filteredEvents.map(event -> modelMapper.map(event, EventDto.class));
    }

    /**
     * Returns {@link EventSpecification} for entered filter parameters.
     *
     * @param filterDto contains data from filters.
     */
    private EventSpecification getSpecification(EventFilterDto filterDto) {
        List<SearchCriteria> searchCriteria = buildSearchCriteria(filterDto);
        return new EventSpecification(searchCriteria);
    }

    /**
     * Build {@link SearchCriteria} based on {@link EventFilterDto}.
     *
     * @param filterDto contains data from filters.
     * @return a list of {@link SearchCriteria}.
     */
    private List<SearchCriteria> buildSearchCriteria(EventFilterDto filterDto) {
        List<SearchCriteria> criteriaList = new ArrayList<>();

        // Add eventLine to criteria
        setValueIfNotEmpty(criteriaList, "eventLine", filterDto.getEventLine() != null ? filterDto.getEventLine().toString() : null);

        // Add eventLocation to criteria
        setValueIfNotEmpty(criteriaList, "eventLocation", filterDto.getEventLocation() != null ? filterDto.getEventLocation() : null);

        // Add eventTime to criteria
        setValueIfNotEmpty(criteriaList, "eventTime", filterDto.getEventTime() != null ? filterDto.getEventTime().toString() : null);

        return criteriaList;
    }

    /**
     * Method that adds new {@link SearchCriteria} if the value is not empty.
     *
     * @param searchCriteriaList - list of existing {@link SearchCriteria}.
     * @param key                - key of the field.
     * @param value              - value of the field.
     */
    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteriaList, String key, String value) {
        if (value != null && !value.isEmpty()) {
            searchCriteriaList.add(SearchCriteria.builder()
                    .key(key)
                    .type(key)
                    .value(value)
                    .build());
        }
    }

    /**
     * Method to upload a list of images for an event. If no images are provided,
     * a default image is added to the event.
     *
     * @param images a list of MultipartFile objects.
     * @return A list of EventImage entities. If no images are provided, a default image is added.
     */
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
    /**
     * Method to send event details via email using a REST client.
     * This method builds an EventSendEmailDto object.
     *
     * @param savedEvent the Event entity containing the event details to send.]
     */
    public void sendEmailDto(Event savedEvent) {
        // Retrieve the access token from the HTTP request header
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);

        // Map the User entity to an AuthorDto object
        PlaceAuthorDto placeAuthorDto = modelMapper.map(savedEvent.getAuthor(), PlaceAuthorDto.class);

        // Get the list of event dates
        List<String> eventDayList = savedEvent.getEventDayDetailsList().stream()
                .map(eventDayDetail -> eventDayDetail.getEventDate().toString())
                .toList();

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
                        .toList())
                .author(placeAuthorDto)
                .secureToken(accessToken)
                .build();

        // Send the event details
        restClient.addEvent(eventSendEmailDto);
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

    @Override
    public PageableDto<SearchEventDto> search(Pageable pageable, String searchQuery, String languageCode) {
        Page<Event> page = this.eventSearchRepo.find(pageable, searchQuery, languageCode);
        List<SearchEventDto> searchEventDtoList = page.getContent()
                .stream()
                .map(event -> this.modelMapper.map(event, SearchEventDto.class))
                .toList();

        return new PageableDto<>(searchEventDtoList,
                page.getTotalElements(),
                page.getPageable().getPageNumber(),
                page.getTotalPages());
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