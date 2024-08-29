package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ImageListSizeValidation;
import greencity.annotations.ImageSizeValidation;
import greencity.annotations.ImageValidation;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Create new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Bad Request")
    })
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> save(
            @Parameter(description = SwaggerExampleModel.ADD_EVENT, required = true)
            @RequestPart @Valid EventCreationDtoRequest eventCreationDtoRequest,
            @Parameter(description = "Images of the event")
            @RequestPart(required = false) @ImageListSizeValidation(maxSize = 5) List<
                    @ImageSizeValidation(maxSizeMB = 10)
                    @ImageValidation MultipartFile> images,
            BindingResult result,
            @Parameter(description = "Current User")
            @CurrentUser UserVO currentUser) {

        // Check for validation errors
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Save the event
        EventDto savedEvent = eventService.saveEvent(eventCreationDtoRequest, images, currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }
}
