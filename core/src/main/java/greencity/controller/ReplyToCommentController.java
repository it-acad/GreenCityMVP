package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.dto.user.UserVO;
import greencity.service.ReplyToCommentService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reply-to-comment")
@RequiredArgsConstructor
public class ReplyToCommentController {

    private final ReplyToCommentService replyToCommentService;

    @PostMapping("{comment_id}")
    public ResponseEntity<ReplyToCommentDto> save(@PathVariable("comment_id") Long commentId,
                                                  @Valid @RequestBody ReplyToCommentDto replyToCommentDto,
                                                  @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        ReplyToCommentDto savedReply = replyToCommentService.save(replyToCommentDto, commentId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReply);
    }

    @PutMapping()
    public ResponseEntity<ReplyToCommentDto> update(@Valid @RequestBody ReplyToCommentDto replyToCommentDto,
                                                    @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        ReplyToCommentDto updatedReply = replyToCommentService.update(replyToCommentDto, currentUser.getId());
        return ResponseEntity.ok(updatedReply);
    }

    @DeleteMapping()
    public void delete(@RequestParam Long replyToCommentId,
                       @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        replyToCommentService.deleteById(replyToCommentId, currentUser.getId());
    }
}
