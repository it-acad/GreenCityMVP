package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/comments")
@AllArgsConstructor
@Validated
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @Operation(summary = "Add a new comment to the event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddEcoNewsCommentDtoResponse.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{eventId}")
    public ResponseEntity<AddEventCommentDtoResponse> addComment(
            @PathVariable Long eventId,
            @RequestBody @Valid AddEventCommentDtoRequest commentDto,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventCommentService.addComment(eventId, commentDto, user));
    }

    @Operation(summary = "Get all comments for the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
                    content = @Content(array = @ArraySchema
                            (schema = @Schema(implementation = AddEventCommentDtoResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("{eventId}")
    public ResponseEntity<List<AddEventCommentDtoResponse>> getCommentsByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCommentService.getCommentsByEventId(eventId));
    }

    @Operation(summary = "Get info about comment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comment details",
                    content = @Content(schema = @Schema(implementation = AddEventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("{commentId}/details")
    public ResponseEntity<AddEventCommentDtoResponse> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok(eventCommentService.getCommentById(commentId));
    }

    @Operation(summary = "Get the number of comments for the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comment count"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("{eventId}/count")
    public ResponseEntity<Long> showQuantityOfAddedComments(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCommentService.showQuantityOfAddedComments(eventId));
    }
}
