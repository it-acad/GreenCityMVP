package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/events/{eventId}/comments")
@AllArgsConstructor
@Validated
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @Operation(summary = "Add a new comment to the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment added successfully",
                    content = @Content(schema = @Schema(implementation = AddEventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not logged in"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PostMapping
    public ResponseEntity<AddEventCommentDtoResponse> addComment(
            @PathVariable Long eventId,
            @RequestBody @Valid AddEventCommentDtoRequest commentDto,
            @CurrentUser UserVO currentUserVO) {
        return new ResponseEntity<>(eventCommentService.addComment(eventId, commentDto, currentUserVO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all comments for the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
                    content = @Content(array = @ArraySchema
                            (schema = @Schema(implementation = AddEventCommentDtoResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping
    public ResponseEntity<List<AddEventCommentDtoResponse>> getCommentsByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCommentService.getCommentsByEventId(eventId));
    }

    @Operation(summary = "Reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reply added successfully",
                    content = @Content(schema = @Schema(implementation = AddEventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data"),
            @ApiResponse(responseCode = "404", description = "Parent comment not found")
    })
    @PostMapping("/{parentCommentId}/reply")
    public ResponseEntity<AddEventCommentDtoResponse> replyToComment(@PathVariable Long eventId
            ,@PathVariable Long parentCommentId
            ,@RequestBody @Valid AddEventCommentDtoRequest replyDto
            ,@CurrentUser UserVO currentUserVO){
        return new ResponseEntity<>(eventCommentService.replyToComment(eventId, parentCommentId, replyDto, currentUserVO)
                , HttpStatus.CREATED);
    }

    @Operation(summary = "Get the number of comments for the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comment count"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/count")
    public ResponseEntity<Long> showQuantityOfAddedComments(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCommentService.showQuantityOfAddedComments(eventId));
    }
}
