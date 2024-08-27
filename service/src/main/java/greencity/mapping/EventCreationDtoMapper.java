package greencity.mapping;

import greencity.dto.event.EventCreationDtoRequest;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.enums.EventType;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class EventCreationDtoMapper extends AbstractConverter<EventCreationDtoRequest, Event> {
    @Override
    protected Event convert(EventCreationDtoRequest eventCreationDtoRequest) {
         Event event =  Event.builder()
                .eventTitle(eventCreationDtoRequest.getEventTitle())
                .description(eventCreationDtoRequest.getDescription())
                .eventType(EventType.valueOf(eventCreationDtoRequest.getEventType()))
                .images(new ArrayList<>())
                .build();

         event.setEventDayDetailsList(eventCreationDtoRequest.getEventDayDetailsList().stream()
                .map(day -> EventDayDetails.builder()
                        .eventDate(day.getEventDate())
                        .eventStartTime(day.getEventStartTime())
                        .eventEndTime(day.getEventEndTime())
                        .isAllDateDuration(day.isAllDateDuration())
                        .isOnline(day.isOnline())
                        .isOffline(day.isOffline())
                        .offlinePlace(day.getOfflinePlace())
                        .onlinePlace(day.getOnlinePlace())
                        .latitude(day.getLatitude())
                        .longitude(day.getLongitude())
                        .event(event)
                        .build())
                .collect(Collectors.toSet()));

         return event;
    }
}
