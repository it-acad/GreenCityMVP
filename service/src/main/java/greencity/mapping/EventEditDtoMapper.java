package greencity.mapping;

import greencity.dto.event.EventEditDto;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.enums.EventType;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class EventEditDtoMapper extends AbstractConverter<EventEditDto, Event>{
    @Override
    public Event convert(EventEditDto eventEditDto) {
        Event event =  Event.builder()
                .eventTitle(eventEditDto.getEventTitle())
                .description(eventEditDto.getDescription())
                .eventType(EventType.valueOf(eventEditDto.getEventType()))
                .images(new ArrayList<>())
                .build();

        event.setEventDayDetailsList(eventEditDto.getEventDayDetailsList().stream()
                .map(day -> EventDayDetails.builder()
                        .id(day.getId())
                        .eventDate(day.getEventDate())
                        .eventStartTime(day.getEventStartTime())
                        .eventEndTime(day.getEventEndTime())
                        .isAllDateDuration(day.isAllDateDuration())
                        .isOnline(day.isOnline())
                        .isOffline(day.isOffline())
                        .latitude(day.getLatitude())
                        .longitude(day.getLongitude())
                        .offlinePlace(day.getOfflinePlace())
                        .onlinePlace(day.getOnlinePlace())
                        .event(event)
                        .build())
                .collect(Collectors.toSet()));

        return event;
    }
}
