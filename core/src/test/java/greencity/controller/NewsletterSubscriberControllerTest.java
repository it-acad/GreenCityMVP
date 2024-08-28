package greencity.controller;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.service.NewsletterSubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NewsletterSubscriberControllerTest {

    @InjectMocks
    private NewsletterSubscriberController newsletterSubscriberController;

    @Mock
    private NewsletterSubscriberService newsletterSubscriberService;

    private MockMvc mockMvc;
    private static final String newsletterSubscriberControllerLink = "/newsletter-subscribers";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsletterSubscriberController)
                .build();
    }

    @Test
    void NewsletterSubscriber_subscribe_statusIsOk() throws Exception {

        NewsletterSubscriberDto newsletterSubscriberDto = new NewsletterSubscriberDto();
        newsletterSubscriberDto.setEmail("test@gmail.com");

        when(newsletterSubscriberService.subscribe(any(NewsletterSubscriberDto.class))).thenReturn(newsletterSubscriberDto);

        mockMvc.perform(post(newsletterSubscriberControllerLink + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newsletterSubscriberDto.toString()))
                .andExpect(status().isOk());

        verify(newsletterSubscriberService).subscribe(any(NewsletterSubscriberDto.class));
    }

    @Test
    void NewsletterSubscriber_subscribe_statusBadRequest() throws Exception {

        NewsletterSubscriberDto newsletterSubscriberDto = new NewsletterSubscriberDto();
        newsletterSubscriberDto.setEmail("notValidEmail.com");


        mockMvc.perform(post(newsletterSubscriberControllerLink + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newsletterSubscriberDto.toString()))
                .andExpect(status().isBadRequest());

    }
}
