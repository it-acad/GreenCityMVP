package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ImageListSizeValidation;
import greencity.annotations.ImageSizeValidation;
import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;


    @Operation(summary = "Create new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(
            @RequestPart(required = false) @ImageListSizeValidation(maxSize = 5) List<
                    @ImageSizeValidation(maxSizeMB = 10)
                    @ImageValidation MultipartFile> images,
            @RequestPart @Valid EventCreationDtoRequest eventCreationDtoRequest,
            @CurrentUser UserVO currentUser) {

        EventDto savedEvent = eventService.saveEvent(eventCreationDtoRequest, images, currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

}
