package greencity.service;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.entity.NewsletterSubscriber;
import greencity.repository.NewsletterSubscriberRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsletterSubscriberServiceImpl implements NewsletterSubscriberService {
    private final NewsletterSubscriberRepo newsletterSubscriberRepo;
    private final ModelMapper modelMapper;


    @Override
    public NewsletterSubscriberDto subscribe(NewsletterSubscriberDto newsletterSubscriberDto) {
        NewsletterSubscriber newsletterSubscriber = modelMapper.map(newsletterSubscriberDto, NewsletterSubscriber.class);
        newsletterSubscriberRepo.save(newsletterSubscriber);
        return modelMapper.map(newsletterSubscriber, NewsletterSubscriberDto.class);
    }

    @Override
    public NewsletterSubscriberDto findByEmail(NewsletterSubscriberDto newsletterSubscriberDto) {
        NewsletterSubscriber newsletterSubscriber = newsletterSubscriberRepo.findByEmail(newsletterSubscriberDto.getEmail());
        return modelMapper.map(newsletterSubscriber, NewsletterSubscriberDto.class);
    }
}
