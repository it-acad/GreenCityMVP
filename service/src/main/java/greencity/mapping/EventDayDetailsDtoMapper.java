package greencity.mapping;
import greencity.dto.event.EventDayDetailsDto;
import greencity.entity.EventDayDetails;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;


@Component
public class EventDayDetailsDtoMapper extends AbstractConverter<EventDayDetailsDto, EventDayDetails> {
    @Override
    protected EventDayDetails convert(EventDayDetailsDto eventDayDetailsDto) {
        return EventDayDetails.builder()
                .id(eventDayDetailsDto.getId())
                .eventDate(eventDayDetailsDto.getEventDate())
                .eventStartTime(eventDayDetailsDto.getEventStartTime())
                .eventEndTime(eventDayDetailsDto.getEventEndTime())
                .isAllDateDuration(eventDayDetailsDto.isAllDateDuration())
                .isOnline(eventDayDetailsDto.isOnline())
                .isOffline(eventDayDetailsDto.isOffline())
                .offlinePlace(eventDayDetailsDto.getOfflinePlace())
                .onlinePlace(eventDayDetailsDto.getOnlinePlace())
                .build();
    }
}
