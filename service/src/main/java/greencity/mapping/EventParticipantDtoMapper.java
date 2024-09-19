package greencity.mapping;

import greencity.dto.event.EventDayDetailsDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventParticipantDto;
import greencity.dto.user.AuthorDto;
import greencity.entity.EventParticipant;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventParticipantDtoMapper extends AbstractConverter<EventParticipant, EventParticipantDto> {

    @Override
    protected EventParticipantDto convert(EventParticipant eventParticipant) {
        // Map Event entity to EventDto
        EventDto eventDto = EventDto.builder()
                .id(eventParticipant.getEvent().getId())
                .eventTitle(eventParticipant.getEvent().getEventTitle())
                .description(eventParticipant.getEvent().getDescription())
                .eventDayDetailsList(eventParticipant.getEvent().getEventDayDetailsList().stream()
                        .map(day -> EventDayDetailsDto.builder()
                                .id(day.getId())
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
                                .build())
                        .collect(Collectors.toSet()))
                .eventType(eventParticipant.getEvent().getEventType().toString())
                .imagePathList(eventParticipant.getEvent().getImages().stream()
                        .map(image -> image.getImagePath())
                        .toList())
                .author(AuthorDto.builder()
                        .id(eventParticipant.getEvent().getAuthor().getId())
                        .name(eventParticipant.getEvent().getAuthor().getName())
                        .build())
                .build();

        // Build EventParticipantDto
        return EventParticipantDto.builder()
                .id(eventParticipant.getId())
                .event(eventDto) // Add full EventDto here
                .userId(eventParticipant.getUserId())
                .joinedAt(eventParticipant.getJoinedAt())
                .eventRole(eventParticipant.getEventRole().toString())
                .build();
    }

}
