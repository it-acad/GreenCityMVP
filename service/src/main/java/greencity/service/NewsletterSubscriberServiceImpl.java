package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.entity.NewsletterSubscriber;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.EmailNotFoundException;
import greencity.repository.NewsletterSubscriberRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsletterSubscriberServiceImpl implements NewsletterSubscriberService {
    private final NewsletterSubscriberRepo newsletterSubscriberRepo;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public NewsletterSubscriberDto subscribe(NewsletterSubscriberDto newsletterSubscriberDto) {
        if (newsletterSubscriberRepo.existsByEmail(newsletterSubscriberDto.getEmail())) {
            throw new BadRequestException(ErrorMessage.NEWS_SUBSCRIBER_EXIST);
        }

        NewsletterSubscriber newsletterSubscriber = modelMapper.map(newsletterSubscriberDto, NewsletterSubscriber.class);
        newsletterSubscriberRepo.save(newsletterSubscriber);
        return modelMapper.map(newsletterSubscriber, NewsletterSubscriberDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsletterSubscriberDto findByEmail(String email) {
        if (email == null || !newsletterSubscriberRepo.existsByEmail(email)) {
            if (email == null) {
                throw new IllegalArgumentException(ErrorMessage.EMAIL_CANT_BE_NULL);
            } else {
                throw new EmailNotFoundException(ErrorMessage.NEWS_SUBSCRIBER_BY_EMAIL_NOT_FOUND + email);
            }
        }
        NewsletterSubscriber newsletterSubscriber = newsletterSubscriberRepo.findByEmail(email);
        return modelMapper.map(newsletterSubscriber, NewsletterSubscriberDto.class);
    }
}
