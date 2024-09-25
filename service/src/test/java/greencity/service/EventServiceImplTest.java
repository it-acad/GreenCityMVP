package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.user.AuthorDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.entity.User;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    private static final Long EVENT_ID = 1L;
    private static final Long USER_ID = 1L;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FileService fileService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private EventServiceImpl eventService;


    @Test
    void testDeleteEvent_Successful() {
        when(eventRepo.existsById(EVENT_ID))
                .thenReturn(true);
        assertDoesNotThrow(() -> eventService.delete(EVENT_ID, USER_ID));
        verify(eventRepo).deleteById(EVENT_ID);
    }

    @Test
    void testDeleteEvent_EventNotFound() {
        when(eventRepo.existsById(EVENT_ID)).thenReturn(false);
        assertThrows(EventNotFoundException.class,
                () -> eventService.delete(EVENT_ID, USER_ID));
    }

    @Test
    void testUpdateEvent_Successful() {
        Set<EventDayDetails> eventDayDetailsList = new HashSet<>();

        Event currentEvent = Event.builder()
                .id(EVENT_ID)
                .eventTitle("Old Event Title")
                .author(User.builder().id(USER_ID).build())
                .eventDayDetailsList(eventDayDetailsList)
                .build();

        EventEditDto editDto = EventEditDto.builder()
                .eventTitle("Updated Event Title")
                .build();

        Event updatedEvent = Event.builder()
                .id(EVENT_ID)
                .eventTitle(editDto.getEventTitle())
                .author(currentEvent.getAuthor())
                .eventDayDetailsList(eventDayDetailsList)
                .build();

        EventDto updatedEventDto = EventDto.builder()
                .id(EVENT_ID)
                .eventTitle(editDto.getEventTitle())
                .author(AuthorDto.builder().id(USER_ID).build())
                .build();

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(currentEvent));
        when(eventRepo.saveAndFlush(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, EventDto.class)).thenReturn(updatedEventDto);
        when(modelMapper.map(editDto, Event.class)).thenReturn(updatedEvent);

        EventDto result = eventService.update(editDto, USER_ID, EVENT_ID, null);

        assertNotNull(result);
        assertEquals(updatedEventDto.getEventTitle(), result.getEventTitle());
    }

    @Test
    void testUpdateEvent_EventNotFound() {
        EventEditDto editDto = new EventEditDto();
        editDto.setEventTitle("Updated Event Title");

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> eventService.update(editDto, USER_ID, EVENT_ID, null));
    }

    @Test
    void findAllByUserId_EventsForCurrentUserExists_ReturnListOfEventsDto() {
        long userId = 1L;
        Set<Event> events = Set.of(ModelUtils.getEvent());
        when(eventRepo.findAllByAuthorId(1L)).thenReturn(events);
        when(modelMapper.map(any(Event.class), any())).thenReturn(ModelUtils.getEventDto());

        Set<EventDto> result = eventService.findAllByUserId(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllByUserId_EventsForCurrentUserNotExists_ReturnEmptyList() {
        long notValidUserId = 999L;
        doReturn(new HashSet<>()).when(eventRepo).findAllByAuthorId(notValidUserId);

        Set<EventDto> result = eventService.findAllByUserId(notValidUserId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_EventsPresentInDb_ReturnListOfEventsDto() {
        List<Event> events = List.of(ModelUtils.getEvent());
        when(eventRepo.findAll()).thenReturn(events);
        when(modelMapper.map(any(Event.class), any())).thenReturn(ModelUtils.getEventDto());

        Set<EventDto> result = eventService.findAll();

        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_EventsNotPresentInDb_ReturnEmptyList() {
        when(eventRepo.findAll()).thenReturn(new ArrayList<>());

        Set<EventDto> result = eventService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    public void saveEvent_AllDataValid_ReturnEventDto() {
        List<MultipartFile> images = new ArrayList<>(List.of(
                new MultipartFileImpl("image1.png", "image1.png", "image/png", new byte[]{}),
                new MultipartFileImpl("image2.png", "image2.png", "image/png", new byte[]{}),
                new MultipartFileImpl("image3.png", "image3.png", "image/png", new byte[]{})));
        Principal currentUser = ModelUtils.getPrincipal();
        EventCreationDtoRequest eventCreationDtoRequest = ModelUtils.getEventCreationDto();
        Event event = ModelUtils.getEvent();
        PlaceAuthorDto placeAuthorDto = PlaceAuthorDto.builder().id(1L).name("Name").email(currentUser.getName()).build();
        when(userRepo.findByEmail(currentUser.getName())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(modelMapper.map(eventCreationDtoRequest, Event.class)).thenReturn(event);
        when(eventRepo.save(event)).thenReturn(event);
        when(fileService.upload(any(MultipartFile.class))).thenReturn("https://someimageurlpath.com");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("some_security_tocken");
        when(modelMapper.map(event.getAuthor(), PlaceAuthorDto.class)).thenReturn(placeAuthorDto);
        doNothing().when(restClient).addEvent(any());
        when(modelMapper.map(event, EventDto.class)).thenReturn(ModelUtils.getEventDto());


        EventDto result = eventService.saveEvent(ModelUtils.getEventCreationDto(), images, currentUser.getName());

        assertNotNull(result);
    }

    @Test
    public void saveEvent_UserEmailNotExists_ThrowException() {
        String notExistsEmail = "notExistsEmail@some.com";
        when(userRepo.findByEmail(notExistsEmail)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + notExistsEmail));

        assertThatThrownBy(() -> eventService.saveEvent(ModelUtils.getEventCreationDto(), new ArrayList<>(), notExistsEmail))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void saveEvent_EventNotValid_ThrowException() {
        Event notValidEvent = ModelUtils.getEvent();
        notValidEvent.setEventType(null);
        notValidEvent.setEventTitle(null);
        notValidEvent.setAuthor(null);
        when(userRepo.findByEmail(ModelUtils.getPrincipal().getName())).thenReturn(Optional.of(ModelUtils.getUser()));
        when(modelMapper.map(ModelUtils.getEventCreationDto(), Event.class)).thenReturn(notValidEvent);
        when(eventRepo.save(notValidEvent)).thenThrow(new DataIntegrityViolationException("Some error message"));


        assertThatThrownBy(() -> eventService.saveEvent(ModelUtils.getEventCreationDto(), new ArrayList<>(), ModelUtils.getPrincipal().getName()))
                .isInstanceOf(NotSavedException.class);
    }
}
