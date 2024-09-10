package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
@Validated
public class EventCommentController {
    private static final Logger logger = LoggerFactory.getLogger(EventCommentController.class);
    private final EventCommentService commentService;

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
        return new ResponseEntity<>(commentService.addComment(eventId, commentDto, currentUserVO), HttpStatus.CREATED);
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
        return ResponseEntity.ok(commentService.getCommentsByEventId(eventId));
    }

    @Operation(summary = "Get the number of comments for the event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comment count"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/count")
    public ResponseEntity<Long> showQuantityOfAddedComments(@PathVariable Long eventId) {
        return ResponseEntity.ok(commentService.showQuantityOfAddedComments(eventId));
    }

    @Operation(summary = "Save reply to comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = EventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/reply/{commentId}")
    public ResponseEntity<EventCommentDtoResponse> saveReply(@PathVariable("eventId") Long eventId,
                                                             @PathVariable("commentId") Long commentId,
                                                             @Valid @RequestBody EventCommentDtoRequest commentDtoRequest,
                                                             @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Saving comment with commentId: {} and authorId: {}", commentId, currentUser.getId());
        EventCommentDtoResponse savedComment = commentService.saveReply(commentDtoRequest, commentId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @Operation(summary = "Update reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = CommentReturnDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/reply/{commentId}")
    public ResponseEntity<EventCommentDtoResponse> updateReply(@PathVariable("eventId") Long eventId,
                                                               @PathVariable Long commentId,
                                                               @Valid @RequestBody EventCommentDtoRequest commentDtoRequest,
                                                               @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Updating comment with id: {} by authorId: {}", commentId, currentUser.getId());
        EventCommentDtoResponse updatedComment = commentService.updateReply(commentDtoRequest, commentId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(updatedComment);
    }

    @Operation(summary = "Delete reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/reply/{commentId}")
    public void deleteReply(@PathVariable("eventId") Long eventId,
                            @PathVariable Long commentId,
                            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Deleting comment with id: {} by authorId: {}", commentId, currentUser.getId());
        commentService.deleteReplyById(commentId, currentUser.getId());
    }

    @Operation(summary = "Get all replies to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = EventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/allReplies/{commentId}")
    public ResponseEntity<List<EventCommentDtoResponse>> getAllReply(@PathVariable("eventId") Long eventId,
                                                                     @PathVariable Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findAllReplyByCommentId(commentId));
    }
}
