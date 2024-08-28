package greencity.controller;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.service.NewsletterSubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsletter-subscribers")
public class NewsletterSubscriberController {

    private final NewsletterSubscriberService newsletterSubscriberService;

    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterSubscriberDto> subscribe(NewsletterSubscriberDto newsletterSubscriberDto) {
        return ResponseEntity.ok(newsletterSubscriberService.subscribe(newsletterSubscriberDto));
    }

//    @GetMapping("/qr-code")
//    public ResponseEntity<byte[]> generateQRCode() {
//        String qrContent = "https://your-website.com/registration-form-url";
//        ByteArrayOutputStream stream = QRCode
//                .from(qrContent)
//                .withSize(300, 300)
//                .stream();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "image/png");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(stream.toByteArray());
//    }
}
