package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.AppConstant;
import greencity.dto.event.EventDto;
import greencity.annotations.ImageListSizeValidation;
import greencity.annotations.ImageSizeValidation;
import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.event.EventEditDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.MessageResponse;
import greencity.service.EventService;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.event.EventCreationDtoRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;
import java.util.List;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    @Operation(summary = "Create new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Bad Request")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(
            @Parameter(description = SwaggerExampleModel.ADD_EVENT, required = true)
            @RequestPart @Valid EventCreationDtoRequest eventCreationDtoRequest,
            @Parameter(description = "Images of the event")
            @RequestPart(required = false) @ImageListSizeValidation(maxSize = 5) List<
                    @ImageSizeValidation(maxSizeMB = 10)
                    @ImageValidation MultipartFile> images,
            @Parameter(description = "Current User")
            @CurrentUser UserVO currentUser) {

        // Save the event
        EventDto savedEvent = eventService.saveEvent(eventCreationDtoRequest, images, currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    /**
     * Method for getting all events.
     *
     * @return List of {@link EventDto} instances.
     * @author Chernenko Vitaliy
     */
    @Operation(summary = "Find all events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping
    public ResponseEntity<Set<EventDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAll());
    }

    /**
     * Method for getting all events by its owner id.
     *
     * @return List of {@link EventDto} instances.
     * @author Chernenko Vitaliy
     */
    @Operation(summary = "Find all events by its owner id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PreAuthorize("@eventController.isPermitted(#userId)")
    @GetMapping("/{userId}")
    public ResponseEntity<Set<EventDto>> getAllEventsByUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllByUserId(userId));
    }

    public boolean isPermitted(long userId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO user = userService.findByEmail(email);
        return user.getId() == userId;
    }

    @Operation(summary = "Delete event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId,
                                         @CurrentUser UserVO currentUser) {
        eventService.delete(eventId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.builder()
                .message(AppConstant.DELETED).success(true).build());
    }

    @Operation(summary = "Update event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> update(@PathVariable Long eventId,
                                           @RequestPart @Valid EventEditDto eventEditDto,
                                           @RequestPart MultipartFile[] images,
                                           @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.update(eventEditDto, currentUser.getId(), eventId, images));
    }
}
