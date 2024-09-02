package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.user.AuthorDto;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.entity.User;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepo eventRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private EventServiceImp eventService;
    private static final Long EVENT_ID = 1L;
    private static final Long USER_ID = 1L;

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
}
