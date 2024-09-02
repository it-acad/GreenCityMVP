package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventServiceImpTest {

    @Mock
    private EventRepo eventRepo;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private EventServiceImpl eventService;

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
        when(eventRepo.findAllByAuthorId(1L)).thenReturn(new HashSet<>());
        when(modelMapper.map(any(Event.class), any())).thenReturn(ModelUtils.getEventDto());

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
        when(modelMapper.map(any(Event.class), any())).thenReturn(ModelUtils.getEventDto());

        Set<EventDto> result = eventService.findAll();

        assertTrue(result.isEmpty());
    }
}
