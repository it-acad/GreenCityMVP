package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class EventCommentController {
    private static final Logger logger = LoggerFactory.getLogger(EventCommentController.class);
    private final EventCommentService commentService;

    @Operation(summary = "Save reply to comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = EventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/reply/{commentId}")
    public ResponseEntity<EventCommentDtoResponse> saveReply(@PathVariable("commentId") Long commentId,
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
    public ResponseEntity<EventCommentDtoResponse> updateReply(@PathVariable Long commentId,
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
    public void deleteReply(@PathVariable Long commentId,
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
    public ResponseEntity<List<EventCommentDtoResponse>> getAllReply(@PathVariable Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findAllReplyByCommentId(commentId));
    }
}
