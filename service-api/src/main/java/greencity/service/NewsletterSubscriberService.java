package greencity.service;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;

public interface NewsletterSubscriberService {
    NewsletterSubscriberDto subscribe(NewsletterSubscriberDto newsletterSubscriberDto);

    NewsletterSubscriberDto findByEmail(NewsletterSubscriberDto newsletterSubscriberDto);
}
