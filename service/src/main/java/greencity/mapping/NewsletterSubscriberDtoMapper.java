package greencity.mapping;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.entity.NewsletterSubscriber;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NewsletterSubscriberDtoMapper extends AbstractConverter<NewsletterSubscriberDto, NewsletterSubscriber> {

    @Override
    protected NewsletterSubscriber convert(NewsletterSubscriberDto newsletterSubscriberDto) {
        return NewsletterSubscriber.builder()
            .id(newsletterSubscriberDto.getId())
            .email(newsletterSubscriberDto.getEmail())
            .build();
    }
}
