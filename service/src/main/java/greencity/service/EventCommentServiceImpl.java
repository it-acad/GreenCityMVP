package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.CommentNotFoundException;
import greencity.exception.exceptions.InvalidCommentIdException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.mapping.EventCommentDtoRequestMapper;
import greencity.mapping.EventCommentResponseMapper;
import greencity.repository.EventCommentRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCommentServiceImpl implements EventCommentService{
    private final UserRepo userRepo;
    private final EventCommentRepo eventCommentRepo;
    private final EventCommentResponseMapper responseMapper;
    private final EventCommentDtoRequestMapper requestMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventCommentServiceImpl.class.getName());

    @Override
    public EventCommentDtoResponse saveReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId) {
        EventComment parentComment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        User user = userRepo.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));
        EventComment comment = requestMapper.toEntity(commentDtoRequest);
        comment.setAuthor(user);
        comment.setParentComment(parentComment);
        EventComment savedComment = eventCommentRepo.save(comment);
        return responseMapper.toDto(savedComment);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @eventCommentServiceImpl.isOwner(#commentId, #authorId)")
    public EventCommentDtoResponse updateReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId) {
        EventComment existingComment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        existingComment.setContent(commentDtoRequest.getText());
        existingComment.setIsEdited(true);
        EventComment updatedComment = eventCommentRepo.save(existingComment);
        return responseMapper.toDto(updatedComment);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @eventCommentServiceImpl.isOwner(#commentId, #authorId)")
    public void deleteReplyById(Long commentId, Long authorId) {
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        eventCommentRepo.deleteById(commentId);
    }

    @Override
    public List<EventCommentDtoResponse> findAllReplyByCommentId(Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        if (commentId == null || commentId < 0) {
            throw new InvalidCommentIdException(ErrorMessage.INVALID_COMMENT_ID + commentId);
        }

        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        List<EventComment> replies = eventCommentRepo.findAllByEventCommentId(commentId);

        return replies.stream()
                .map(responseMapper::toDto)
                .toList();
    }

    public boolean isOwner(Long commentId, Long userId) {
        EventComment comment = eventCommentRepo.findById(commentId).orElse(null);
        return comment != null && comment.getAuthor().getId().equals(userId);
    }
}
