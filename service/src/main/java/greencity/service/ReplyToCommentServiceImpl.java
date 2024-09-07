package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.replytocomment.ReplyToCommentResponseDto;
import greencity.dto.replytocomment.ReplyToCommentRequestDto;
import greencity.entity.Comment;
import greencity.entity.ReplyToComment;
import greencity.entity.User;
import greencity.exception.exceptions.*;
import greencity.mapping.ReplyToCommentResponseMapper;
import greencity.mapping.ReplyToCommentRequestDtoMapper;
import greencity.repository.CommentRepo;
import greencity.repository.ReplyToCommentRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReplyToCommentServiceImpl implements ReplyToCommentService {

    private final ReplyToCommentRepo replyToCommentRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final ReplyToCommentResponseMapper responseMapper;
    private final ReplyToCommentRequestDtoMapper requestMapper;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(http|https|ftp|ftps)://[^\\s/$.?#].\\S*",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\\u203C-\\u3299\\uD83C\\uD000-\\uDFFF\\uD83D\\uD000-\\uDFFF\\uD83E\\uD000-\\uDFFF]");
    private static final Logger logger = LoggerFactory.getLogger(ReplyToCommentServiceImpl.class.getName());



    /**
     * Method to save {@link greencity.entity.EcoNewsComment} to the database.
     *
     * @param replyToCommentRequestDto - dto for {@link greencity.entity.ReplyToComment}.
     * @param commentId         - id of {@link greencity.entity.Comment}.
     * @param authorId          - id of {@link greencity.entity.User}.
     * @return {@link ReplyToCommentResponseDto} - saved {@link greencity.entity.ReplyToComment} as a dto.
     */
    @Override
    @Transactional
    public ReplyToCommentResponseDto save(ReplyToCommentRequestDto replyToCommentRequestDto, Long commentId, Long authorId) {
        logger.info("Saving reply to comment with commentId: {} and authorId: {}", commentId, authorId);
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));

        checkContent(replyToCommentRequestDto.getContent());
        ReplyToComment replyToComment = requestMapper.toEntity(replyToCommentRequestDto);

        replyToComment.setComment(comment);
        replyToComment.setAuthor(author);

        return responseMapper.toDto(replyToCommentRepo.save(replyToComment));
    }

    /**
     * Method to update {@link greencity.entity.ReplyToComment} in the database.
     *
     * @param replyToCommentRequestDto - dto for {@link greencity.entity.ReplyToComment}.
     * @param authorId - id of {@link greencity.entity.User}.
     *
     * @return {@link ReplyToCommentResponseDto} - updated {@link greencity.entity.ReplyToComment} as a dto.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @replyToCommentServiceImpl.isOwner(#replyToCommentId, #authorId)")
    @Transactional
    public ReplyToCommentResponseDto update(ReplyToCommentRequestDto replyToCommentRequestDto, Long replyToCommentId,Long authorId) {
        logger.info("Updating reply to comment with id: {} by authorId: {}", replyToCommentId, authorId);
        checkContent(replyToCommentRequestDto.getContent());

        ReplyToComment updatedReply = replyToCommentRepo.findById(replyToCommentId)
                .orElseThrow(() -> new ReplyNotFoundException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentId));

        updatedReply.setContent(replyToCommentRequestDto.getContent());
        updatedReply.setIsEdited(true);

        return responseMapper.toDto(replyToCommentRepo.save(updatedReply));
    }

    /**
     * Method to delete {@link greencity.entity.ReplyToComment} from the database.
     *
     * @param replyToCommentId - id of {@link greencity.entity.ReplyToComment}.
     * @param authorId - id of {@link greencity.entity.User}.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN') or @replyToCommentServiceImpl.isOwner(#replyToCommentId, #authorId)")
    @Transactional
    public void deleteById(Long replyToCommentId, Long authorId) {
        logger.info("Deleting reply to comment with id: {} by authorId: {}", replyToCommentId, authorId);
        ReplyToComment replyToComment = replyToCommentRepo.findById(replyToCommentId)
                .orElseThrow(() -> new ReplyNotFoundException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentId));

        replyToCommentRepo.deleteById(replyToCommentId);
    }

    /**
     * Method to find all {@link greencity.entity.ReplyToComment} by {@link greencity.entity.Comment} id.
     *
     * @param commentId - id of {@link greencity.entity.Comment}.
     *
     * @return list of {@link ReplyToCommentResponseDto}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReplyToCommentResponseDto> findAllByCommentId(Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        if (commentId == null || commentId < 0) {
            throw new InvalidCommentIdException(ErrorMessage.INVALID_COMMENT_ID + commentId);
        }

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        List<ReplyToComment> replyToComments = replyToCommentRepo.findAllByCommentId(commentId);

        return replyToComments.stream()
                .map(responseMapper::toDto)
                .toList();
    }

    private void checkContent(String content) {
        if (URL_PATTERN.matcher(content).find()) {
            throw new ContentContainsURLException(ErrorMessage.ENABLE_TO_CONTAIN_URL);
        }
        if (EMOJI_PATTERN.matcher(content).find()) {
            throw new ContentContainsEmojiException(ErrorMessage.ENABLE_TO_CONTAIN_EMOJI);
        }
    }

    public boolean isOwner(Long replyToCommentId, Long currentUserId) {
        return replyToCommentRepo.findById(replyToCommentId)
                .map(replyToComment -> replyToComment.getAuthor().getId().equals(currentUserId))
                .orElseThrow(() -> new ReplyNotFoundException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentId));
    }
}
