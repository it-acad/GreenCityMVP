package greencity.service;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;

import java.util.Optional;

public interface NewsletterSubscriberService {
    NewsletterSubscriberDto subscribe(NewsletterSubscriberDto newsletterSubscriberDto);

    NewsletterSubscriberDto findByEmail(String email);
}
