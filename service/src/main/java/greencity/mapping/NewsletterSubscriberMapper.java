package greencity.mapping;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.entity.NewsletterSubscriber;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NewsletterSubscriberMapper extends AbstractConverter<NewsletterSubscriber, NewsletterSubscriberDto> {

    @Override
    protected NewsletterSubscriberDto convert(NewsletterSubscriber newsletterSubscriber) {
        return NewsletterSubscriberDto.builder()
            .id(newsletterSubscriber.getId())
            .email(newsletterSubscriber.getEmail())
            .build();
    }
}
