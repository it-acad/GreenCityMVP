package greencity.service;

import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventServiceImp implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;


    public EventDto save(final MultipartFile[] images, final EventCreationDto eventCreationDto, final Long userId) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author Chernenko Vitaliy.
     */
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
}