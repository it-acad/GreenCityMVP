package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.EventDto;
import greencity.dto.user.AuthorDto;
import greencity.entity.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EventDtoMapperTest {

    @InjectMocks
    private EventDtoMapper mapper;

    @Test
    void convertTest() {
        // Arrange
        Event event = ModelUtils.getEvent();

        EventDto expected = EventDto.builder()
                .id(event.getId())
                .eventTitle(event.getEventTitle())
                .description(event.getDescription())
                .eventDayDetailsList(Set.of(ModelUtils.getEventDayDetailsDto()))
                .eventType(event.getEventType().toString())
                .imagePathList(new ArrayList<>(List.of(
                        "https://someimageurl1.net",
                        "https://someimageurl2.net",
                        "https://someimageurl3.net"
                )))
                .author(new AuthorDto(event.getAuthor().getId(), event.getAuthor().getName())).build();

        // Act
        EventDto actual = mapper.convert(event);

        // Assert
        assertEquals(actual, expected);
    }
}
