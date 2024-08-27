package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.EventCreationDto;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.enums.EventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EventCreationDtoMapperTest {


    @InjectMocks
    private EventCreationDtoMapper mapper;


    @Test
    void convertTest() {
        // Arrange
        EventCreationDto eventCreationDto = ModelUtils.getEventCreationDto();

        Event expected = Event.builder()
                .eventTitle(eventCreationDto.getEventTitle())
                .description(eventCreationDto.getDescription())
                .eventType(EventType.valueOf(eventCreationDto.getEventType()))
                .eventDayDetailsList(Set.of(ModelUtils.getEventDayDetails()))
                .images(new ArrayList<>(List.of(
                        EventImage.builder().id(1L).imagePath("https://someimageurl1.net").build(),
                        EventImage.builder().id(2L).imagePath("https://someimageurl2.net").build(),
                        EventImage.builder().id(3L).imagePath("https://someimageurl3.net").build()
                )))
                .build();

        expected.getEventDayDetailsList().forEach(day -> day.setEvent(expected));

        // Act
        Event actual = mapper.convert(eventCreationDto);

        // Assert
        assertEquals(actual, expected);
    }
}
