package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.user.UserVO;
import greencity.service.CommentService;
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
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    /**
     * Saves a new reply to a comment. Returns the saved comment with a 201 Created status.
     *
     * @param commentId - ID of the comment to reply to.
     * @param commentDto - DTO with reply details.
     * @param currentUser - Authenticated user making the request.
     *
     * @return {@link ResponseEntity}&lt;{@link CommentReturnDto}&gt; - Response with saved comment.
     *
     * @response 201 - Created comment.
     * @response 400 - Bad request if data is invalid.
     * @response 401 - Unauthorized if not authenticated.
     */
    @Operation(summary = "Save reply to comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = CommentReturnDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/{commentId}")
    public ResponseEntity<CommentReturnDto> save(@PathVariable("commentId") Long commentId,
                                                 @Valid @RequestBody CommentDto commentDto,
                                                 @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Saving comment with commentId: {} and authorId: {}", commentId, currentUser.getId());
        CommentReturnDto savedComment = commentService.save(commentDto, commentId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    /**
     * Updates a reply to a comment. Returns the updated comment with a 200 OK status.
     *
     * @param commentId - ID of the comment to update.
     * @param commentDto - DTO with updated comment details.
     * @param currentUser - Authenticated user making the request.
     *
     * @return {@link ResponseEntity}&lt;{@link CommentReturnDto}&gt; - Response with updated comment.
     *
     * @response 200 - Updated comment.
     * @response 400 - Bad request if data is invalid.
     * @response 401 - Unauthorized if not authenticated.
     * @response 403 - Forbidden if user lacks permissions.
     */
    @Operation(summary = "Update reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = CommentReturnDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentReturnDto> update(@PathVariable Long commentId,
                                                   @Valid @RequestBody CommentDto commentDto,
                                                   @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Updating comment with id: {} by authorId: {}", commentId, currentUser.getId());
        CommentReturnDto updatedComment = commentService.update(commentDto, commentId, currentUser.getId());
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Deletes a reply to a comment.
     *
     * @param commentId - ID of the comment to delete.
     * @param currentUser - Authenticated user making the request.
     *
     * @response 400 - Bad request if the ID is invalid.
     * @response 401 - Unauthorized if not authenticated.
     * @response 403 - Forbidden if user lacks permissions.
     */
    @Operation(summary = "Delete reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long commentId,
                       @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Deleting comment with id: {} by authorId: {}", commentId, currentUser.getId());
        commentService.deleteById(commentId, currentUser.getId());
    }

    /**
     * Retrieves all replies to a comment.
     *
     * @param commentId - ID of the comment whose replies are to be fetched.
     *
     * @return List&lt;{@link CommentReturnDto}&gt; - List of replies as DTOs.
     *
     * @response 200 - Found replies.
     * @response 400 - Bad request if the ID is invalid.
     * @response 401 - Unauthorized if not authenticated.
     * @response 404 - Not found if the comment is not found.
     */
    @Operation(summary = "Get all replies to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = CommentReturnDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/allReplies/{commentId}")
    public ResponseEntity<List<CommentReturnDto>> getAll(@PathVariable Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        return ResponseEntity.ok(commentService.findAllByCommentId(commentId));
    }
}
