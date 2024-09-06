package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.Comment;
import greencity.entity.ReplyToComment;
import greencity.entity.User;
import greencity.exception.exceptions.*;
import greencity.mapping.ReplyToCommentMapper;
import greencity.repository.CommentRepo;
import greencity.repository.ReplyToCommentRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
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
    private final ReplyToCommentMapper mapper;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(http|https|ftp|ftps)://[^\\s/$.?#].\\S*",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\\u203C-\\u3299\\uD83C\\uD000-\\uDFFF\\uD83D\\uD000-\\uDFFF\\uD83E\\uD000-\\uDFFF]");



    /**
     * Method to save {@link greencity.entity.EcoNewsComment} to the database.
     *
     * @param replyToCommentDto - dto for {@link greencity.entity.ReplyToComment}.
     * @param commentId         - id of {@link greencity.entity.Comment}.
     * @param authorId          - id of {@link greencity.entity.User}.
     * @return {@link ReplyToCommentDto} - saved {@link greencity.entity.ReplyToComment} as a dto.
     */
    @Override
    @Transactional
    public ReplyToCommentDto save(ReplyToCommentDto replyToCommentDto, Long commentId, Long authorId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));

        checkContent(replyToCommentDto.getContent());
        ReplyToComment replyToComment = mapper.toEntity(replyToCommentDto);

        replyToComment.setComment(comment);
        replyToComment.setAuthor(author);

        return mapper.toDto(replyToCommentRepo.save(replyToComment));
    }

    /**
     * Method to update {@link greencity.entity.ReplyToComment} in the database.
     *
     * @param replyToCommentDto - dto for {@link greencity.entity.ReplyToComment}.
     * @param authorId - id of {@link greencity.entity.User}.
     *
     * @return {@link ReplyToCommentDto} - updated {@link greencity.entity.ReplyToComment} as a dto.
     */
    @Override
    @Transactional
    public ReplyToCommentDto update(ReplyToCommentDto replyToCommentDto, Long authorId) {
        checkContent(replyToCommentDto.getContent());

        ReplyToComment updatedReply = replyToCommentRepo.findById(replyToCommentDto.getId())
                .orElseThrow(() -> new ReplyNotFoundException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentDto.getId()));

        if (!updatedReply.getAuthor().getId().equals(authorId)) {
            throw new UnauthorizedReplyUpdateException(ErrorMessage.ENABLE_TO_UPDATE_REPLY);
        }

        updatedReply.setContent(replyToCommentDto.getContent());
        updatedReply.setIsEdited(true);

        return mapper.toDto(replyToCommentRepo.save(updatedReply));
    }

    /**
     * Method to delete {@link greencity.entity.ReplyToComment} from the database.
     *
     * @param replyToCommentId - id of {@link greencity.entity.ReplyToComment}.
     * @param authorId - id of {@link greencity.entity.User}.
     */
    @Override
    @Transactional
    public void deleteById(Long replyToCommentId, Long authorId) {

        ReplyToComment replyToComment = replyToCommentRepo.findById(replyToCommentId)
                .orElseThrow(() -> new ReplyNotFoundException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentId));

        if (!replyToComment.getAuthor().getId().equals(authorId)) {
            throw new UnauthorizedReplyDeleteException(ErrorMessage.ENABLE_TO_DELETE_REPLY);
        }

        replyToCommentRepo.deleteById(replyToCommentId);
    }

    /**
     * Method to find all {@link greencity.entity.ReplyToComment} by {@link greencity.entity.Comment} id.
     *
     * @param commentId - id of {@link greencity.entity.Comment}.
     *
     * @return list of {@link ReplyToCommentDto}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReplyToCommentDto> findAllByCommentId(Long commentId) {
        if (commentId == null || commentId < 0) {
            throw new InvalidCommentIdException(ErrorMessage.INVALID_COMMENT_ID + commentId);
        }

        List<ReplyToComment> replyToComments = replyToCommentRepo.findAllByCommentId(commentId);

        return replyToComments.stream()
                .map(mapper::toDto)
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
}
