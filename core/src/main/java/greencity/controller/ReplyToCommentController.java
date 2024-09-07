package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.replytocomment.ReplyToCommentRequestDto;
import greencity.dto.replytocomment.ReplyToCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ReplyToCommentService;
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
@RequestMapping("/reply-to-comment")
@RequiredArgsConstructor
public class ReplyToCommentController {
    private static final Logger logger = LoggerFactory.getLogger(ReplyToCommentController.class);
    private final ReplyToCommentService replyToCommentService;

    /**
     * Method for saving reply to comment.
     *
     * @param commentId id of comment
     * @return {@link greencity.dto.replytocomment.ReplyToCommentResponseDto}
     */
    @Operation(summary = "Save reply to comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/reply/{commentId}")
    public ResponseEntity<ReplyToCommentResponseDto> save(@PathVariable("commentId") Long commentId,
                                                          @Valid @RequestBody ReplyToCommentRequestDto replyToCommentDto,
                                                          @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Saving reply to comment with commentId: {} and authorId: {}", commentId, currentUser.getId());
        ReplyToCommentResponseDto savedReply = replyToCommentService.save(replyToCommentDto, commentId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReply);
    }

    /**
     * Method for updating reply to comment.
     *
     * @return {@link ReplyToCommentResponseDto}
     */
    @Operation(summary = "Update reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = ReplyToCommentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("/{replyToCommentId}")
    public ResponseEntity<ReplyToCommentResponseDto> update(
                                                    @PathVariable Long replyToCommentId,
                                                    @Valid @RequestBody ReplyToCommentRequestDto replyToCommentDto,
                                                    @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Updating reply to comment with id: {} by authorId: {}", replyToCommentDto.getId(), currentUser.getId());
        ReplyToCommentResponseDto updatedReply = replyToCommentService.update(replyToCommentDto, replyToCommentId, currentUser.getId());
        return ResponseEntity.ok(updatedReply);
    }

    /**
     * Method for deleting reply to comment.
     *
     */
    @Operation(summary = "Delete reply to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/{replyToCommentId}")
    public void delete(
                       @PathVariable Long replyToCommentId,
                       @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        logger.info("Deleting reply to comment with id: {} by authorId: {}", replyToCommentId, currentUser.getId());
        replyToCommentService.deleteById(replyToCommentId, currentUser.getId());
    }

    /**
     * Method for getting all replies to comment.
     *
     * @return {@link List<ReplyToCommentResponseDto>}
     */
    @Operation(summary = "Get all replies to comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = ReplyToCommentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/allReplies/{commentId}")
    public ResponseEntity<List<ReplyToCommentResponseDto>> getAll(@PathVariable Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        return ResponseEntity.ok(replyToCommentService.findAllByCommentId(commentId));
    }
}
