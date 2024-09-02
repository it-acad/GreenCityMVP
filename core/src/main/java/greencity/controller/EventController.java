package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.AppConstant;
import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventEditDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.MessageResponse;
import greencity.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(@RequestPart MultipartFile[] images,
                                         @RequestPart EventCreationDto eventCreationDto,
                                         @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(images, eventCreationDto, currentUser.getId()));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId,
                                         @CurrentUser UserVO currentUser) {
        eventService.delete(eventId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.builder()
                .message(AppConstant.DELETED).success(true).build());
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> update(@PathVariable Long eventId,
                                           @RequestPart EventEditDto eventEditDto,
                                           @RequestPart MultipartFile[] images,
                                           @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.update(eventEditDto, currentUser.getId(), eventId, images));
    }
}
