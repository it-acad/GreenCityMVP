package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(@RequestPart MultipartFile[] images,
                                         @RequestPart @Valid EventCreationDto eventCreationDto,
                                         @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(images, eventCreationDto, currentUser.getId()));
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

}
