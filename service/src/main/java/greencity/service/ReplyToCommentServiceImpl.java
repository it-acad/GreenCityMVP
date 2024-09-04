package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.Comment;
import greencity.entity.ReplyToComment;
import greencity.entity.User;
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
public class ReplyToCommentServiceImpl implements ReplyToCommentService{

    private final ReplyToCommentRepo replyToCommentRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final ReplyToCommentMapper mapper;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(http|https|ftp|ftps)://[^\\s/$.?#].\\S*",
            Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional
    public ReplyToCommentDto save(ReplyToCommentDto replyToCommentDto, Long commentId, Long authorId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));

        checkContent(replyToCommentDto.getContent());
        ReplyToComment replyToComment = mapper.toEntity(replyToCommentDto);

        replyToComment.setComment(comment);
        replyToComment.setAuthor(author);

        return mapper.toDto(replyToCommentRepo.save(replyToComment));
    }

    @Override
    @Transactional
    public ReplyToCommentDto update(ReplyToCommentDto replyToCommentDto, Long authorId) {
        checkContent(replyToCommentDto.getContent());

        ReplyToComment updatedReply = replyToCommentRepo.findById(replyToCommentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentDto.getId()));

        if (!updatedReply.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException(ErrorMessage.ENABLE_TO_UPDATE_REPLY);
        }

        updatedReply.setContent(replyToCommentDto.getContent());
        updatedReply.setIsEdited(true);

        return mapper.toDto(replyToCommentRepo.save(updatedReply));
    }

    @Override
    @Transactional
    public void deleteById(Long replyToCommentId, Long authorId) {

        ReplyToComment replyToComment = replyToCommentRepo.findById(replyToCommentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.REPLY_NOT_FOUND_BY_ID + replyToCommentId));

        if (!replyToComment.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException(ErrorMessage.ENABLE_TO_DELETE_REPLY);
        }

        replyToCommentRepo.deleteById(replyToCommentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyToCommentDto> findAllByCommentId(Long commentId) {
        List<ReplyToComment> replyToComments = replyToCommentRepo.findAllByCommentId(commentId);

        return replyToComments.stream()
                .map(mapper::toDto)
                .toList();
    }

    private void checkContent(String content) {
        if (URL_PATTERN.matcher(content).find()) {
            throw new IllegalArgumentException(ErrorMessage.ENABLE_TO_CONTAIN_URL);
        }
    }
}
