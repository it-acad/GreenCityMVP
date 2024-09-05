package greencity.service;

import greencity.dto.replytocomment.ReplyToCommentDto;
import greencity.entity.Comment;
import greencity.entity.ReplyToComment;
import greencity.entity.User;
import greencity.exception.exceptions.*;
import greencity.mapping.ReplyToCommentMapper;
import greencity.repository.CommentRepo;
import greencity.repository.ReplyToCommentRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReplyToCommentServiceImplTest {

    @Mock
    private ReplyToCommentRepo replyToCommentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ReplyToCommentMapper mapper;

    @Mock
    private CommentRepo commentRepo;

    @InjectMocks
    private ReplyToCommentServiceImpl replyToCommentService;

    private ReplyToComment replyToComment;
    private ReplyToCommentDto replyToCommentDto;

    @BeforeEach
    public void setUp() {
        replyToComment = createReplyToComment("content", 1L);
        replyToCommentDto = createReplyToCommentDto("content");
    }

    private ReplyToComment createReplyToComment(String content, Long authorId) {
        ReplyToComment reply = new ReplyToComment();
        reply.setContent(content);
        reply.setAuthor(createUser(authorId));
        return reply;
    }

    private ReplyToCommentDto createReplyToCommentDto(String content) {
        ReplyToCommentDto dto = new ReplyToCommentDto();
        dto.setContent(content);
        return dto;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @Test
    void save_ValidData_ReturnsSavedReply() {
        Long commentId = 1L;
        Long authorId = 1L;

        Comment comment = new Comment();
        ReplyToCommentDto savedReplyDto = createReplyToCommentDto("content");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepo.findById(authorId)).thenReturn(Optional.of(replyToComment.getAuthor()));
        when(mapper.toEntity(replyToCommentDto)).thenReturn(replyToComment);
        when(replyToCommentRepo.save(replyToComment)).thenReturn(replyToComment);
        when(mapper.toDto(replyToComment)).thenReturn(savedReplyDto);

        ReplyToCommentDto result = replyToCommentService.save(replyToCommentDto, commentId, authorId);

        assertEquals(savedReplyDto, result);
    }

    @Test
    void save_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                replyToCommentService.save(replyToCommentDto, invalidCommentId, authorId));
    }

    @Test
    void save_InvalidAuthorId_ThrowsUserNotFoundException() {
        Long commentId = 1L;
        Long invalidAuthorId = 1L;

        Comment comment = new Comment();
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepo.findById(invalidAuthorId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                replyToCommentService.save(replyToCommentDto, commentId, invalidAuthorId));
    }

    @Test
    void update_ValidData_ReturnsUpdatedReply() {
        Long replyId = 1L;
        Long authorId = 1L;
        ReplyToCommentDto updatedDto = createReplyToCommentDto("Updated content");
        updatedDto.setId(replyId);

        ReplyToComment existingReply = createReplyToComment("Old content", authorId);
        existingReply.setId(replyId);

        ReplyToComment updatedReply = createReplyToComment("Updated content", authorId);
        updatedReply.setId(replyId);
        updatedReply.setIsEdited(true);

        ReplyToCommentDto updatedReplyDto = createReplyToCommentDto("Updated content");
        updatedReplyDto.setId(replyId);

        when(replyToCommentRepo.findById(replyId)).thenReturn(Optional.of(existingReply));
        when(mapper.toDto(updatedReply)).thenReturn(updatedReplyDto);
        when(replyToCommentRepo.save(any(ReplyToComment.class))).thenReturn(updatedReply);

        ReplyToCommentDto result = replyToCommentService.update(updatedDto, authorId);

        assertEquals(updatedReplyDto, result);
    }

    @Test
    void update_InvalidReplyId_ThrowsReplyNotFoundException() {
        Long invalidReplyId = 1L;
        Long authorId = 1L;
        ReplyToCommentDto replyToCommentDto = createReplyToCommentDto("Updated content");
        replyToCommentDto.setId(invalidReplyId);

        when(replyToCommentRepo.findById(invalidReplyId)).thenReturn(Optional.empty());

        assertThrows(ReplyNotFoundException.class, () ->
                replyToCommentService.update(replyToCommentDto, authorId));
    }

    @Test
    void update_UnauthorizedUpdate_ThrowsUnauthorizedReplyUpdateException() {
        Long replyId = 1L;
        Long unauthorizedAuthorId = 2L;
        ReplyToCommentDto replyToCommentDto = createReplyToCommentDto("Updated content");
        replyToCommentDto.setId(replyId);

        ReplyToComment existingReply = createReplyToComment("Old content", 1L);
        existingReply.setId(replyId);

        when(replyToCommentRepo.findById(replyId)).thenReturn(Optional.of(existingReply));

        assertThrows(UnauthorizedReplyUpdateException.class, () ->
                replyToCommentService.update(replyToCommentDto, unauthorizedAuthorId));
    }

    @Test
    void deleteById_ValidIdAndAuthorizedUser_DeletesReply() {
        Long replyToCommentId = 1L;
        Long authorId = 1L;
        ReplyToComment replyToComment = createReplyToComment("content", authorId);
        replyToComment.setId(replyToCommentId);

        when(replyToCommentRepo.findById(replyToCommentId)).thenReturn(Optional.of(replyToComment));

        replyToCommentService.deleteById(replyToCommentId, authorId);

        verify(replyToCommentRepo).deleteById(replyToCommentId);
    }

    @Test
    void deleteById_InvalidId_ThrowsReplyNotFoundException() {
        Long invalidReplyToCommentId = 1L;
        Long authorId = 1L;

        when(replyToCommentRepo.findById(invalidReplyToCommentId)).thenReturn(Optional.empty());

        assertThrows(ReplyNotFoundException.class, () ->
                replyToCommentService.deleteById(invalidReplyToCommentId, authorId));
    }

    @Test
    void deleteById_UnauthorizedUser_ThrowsUnauthorizedReplyDeleteException() {
        Long replyToCommentId = 1L;
        Long unauthorizedAuthorId = 2L;
        ReplyToComment replyToComment = createReplyToComment("content", 1L);
        replyToComment.setId(replyToCommentId);

        when(replyToCommentRepo.findById(replyToCommentId)).thenReturn(Optional.of(replyToComment));

        assertThrows(UnauthorizedReplyDeleteException.class, () ->
                replyToCommentService.deleteById(replyToCommentId, unauthorizedAuthorId));
    }

    @Test
    void findAllByCommentId_ValidCommentId_ReturnsReplyToCommentDtos() {
        Long commentId = 1L;
        ReplyToComment replyToComment = createReplyToComment("Test reply", 1L);
        replyToComment.setId(1L);

        ReplyToCommentDto replyToCommentDto = createReplyToCommentDto("Test reply");
        replyToCommentDto.setId(1L);

        List<ReplyToComment> replyToComments = List.of(replyToComment);
        List<ReplyToCommentDto> replyToCommentDtos = List.of(replyToCommentDto);

        when(replyToCommentRepo.findAllByCommentId(commentId)).thenReturn(replyToComments);
        when(mapper.toDto(replyToComment)).thenReturn(replyToCommentDto);

        List<ReplyToCommentDto> result = replyToCommentService.findAllByCommentId(commentId);

        assertEquals(replyToCommentDtos, result);
    }

    @Test
    void findAllByCommentId_InvalidCommentId_ThrowsInvalidCommentIdException() {
        Long invalidCommentId = -1L;

        assertThrows(InvalidCommentIdException.class, () ->
                replyToCommentService.findAllByCommentId(invalidCommentId));
    }

    @Test
    void findAllByCommentId_NullCommentId_ThrowsInvalidCommentIdException() {
        Long nullCommentId = null;

        assertThrows(InvalidCommentIdException.class, () ->
                replyToCommentService.findAllByCommentId(nullCommentId));
    }
}
