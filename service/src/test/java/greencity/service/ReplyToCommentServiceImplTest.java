package greencity.service;

import greencity.ModelUtils;
import greencity.dto.replytocomment.ReplyToCommentRequestDto;
import greencity.dto.replytocomment.ReplyToCommentResponseDto;
import greencity.entity.Comment;
import greencity.entity.ReplyToComment;
import greencity.exception.exceptions.*;
import greencity.mapping.ReplyToCommentResponseMapper;
import greencity.mapping.ReplyToCommentRequestDtoMapper;
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
    private ReplyToCommentResponseMapper responseMapper;

    @Mock
    private ReplyToCommentRequestDtoMapper requestMapper;

    @Mock
    private CommentRepo commentRepo;

    @InjectMocks
    private ReplyToCommentServiceImpl replyToCommentService;

    private ReplyToComment replyToComment;
    private ReplyToCommentRequestDto replyToCommentRequestDto;

    @BeforeEach
    public void setUp() {
        replyToComment = createReplyToComment("content");
        replyToCommentRequestDto = createReplyToCommentRequestDto("content");
    }

    private ReplyToComment createReplyToComment(String content) {
        ReplyToComment reply = new ReplyToComment();
        reply.setContent(content);
        reply.setAuthor(ModelUtils.getUser());
        return reply;
    }

    private ReplyToCommentRequestDto createReplyToCommentRequestDto(String content) {
        ReplyToCommentRequestDto dto = new ReplyToCommentRequestDto();
        dto.setContent(content);
        return dto;
    }

    @Test
    void save_ValidData_ReturnsSavedReply() {
        Long commentId = 1L;
        Long authorId = 1L;

        Comment comment = new Comment();
        ReplyToCommentResponseDto savedReplyDto = new ReplyToCommentResponseDto();
        savedReplyDto.setContent("content");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepo.findById(authorId)).thenReturn(Optional.of(replyToComment.getAuthor()));
        when(requestMapper.toEntity(replyToCommentRequestDto)).thenReturn(replyToComment);
        when(replyToCommentRepo.save(replyToComment)).thenReturn(replyToComment);
        when(responseMapper.toDto(replyToComment)).thenReturn(savedReplyDto);

        ReplyToCommentResponseDto result = replyToCommentService.save(replyToCommentRequestDto, commentId, authorId);

        assertEquals(savedReplyDto, result);
    }

    @Test
    void save_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                replyToCommentService.save(replyToCommentRequestDto, invalidCommentId, authorId));
    }

    @Test
    void save_InvalidAuthorId_ThrowsUserNotFoundException() {
        Long commentId = 1L;
        Long invalidAuthorId = 1L;

        Comment comment = new Comment();
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UserNotFoundException.class, () ->
                replyToCommentService.save(replyToCommentRequestDto, commentId, invalidAuthorId));
    }

    @Test
    void update_ValidData_ReturnsUpdatedReply() {
        Long replyId = 1L;
        Long authorId = 1L;
        ReplyToCommentRequestDto updatedDto = createReplyToCommentRequestDto("Updated content");
        updatedDto.setId(replyId);

        ReplyToComment existingReply = createReplyToComment("Old content");
        existingReply.setId(replyId);

        ReplyToComment updatedReply = createReplyToComment("Updated content");
        updatedReply.setId(replyId);
        updatedReply.setIsEdited(true);

        ReplyToCommentResponseDto updatedReplyDto = new ReplyToCommentResponseDto();
        updatedReplyDto.setContent("Updated content");
        updatedReplyDto.setId(replyId);

        when(replyToCommentRepo.findById(replyId)).thenReturn(Optional.of(existingReply));
        when(responseMapper.toDto(updatedReply)).thenReturn(updatedReplyDto);
        when(replyToCommentRepo.save(any(ReplyToComment.class))).thenReturn(updatedReply);

        ReplyToCommentResponseDto result = replyToCommentService.update(updatedDto, authorId);

        assertEquals(updatedReplyDto, result);
    }

    @Test
    void update_InvalidReplyId_ThrowsReplyNotFoundException() {
        Long invalidReplyId = 1L;
        Long authorId = 1L;
        ReplyToCommentRequestDto replyToCommentRequestDto1 = createReplyToCommentRequestDto("Updated content");
        replyToCommentRequestDto1.setId(invalidReplyId);

        when(replyToCommentRepo.findById(invalidReplyId)).thenReturn(Optional.empty());

        assertThrows(ReplyNotFoundException.class, () ->
                replyToCommentService.update(replyToCommentRequestDto1, authorId));
    }

    @Test
    void update_UnauthorizedUpdate_ThrowsUnauthorizedReplyUpdateException() {
        Long replyId = 1L;
        Long unauthorizedAuthorId = 2L;
        ReplyToCommentRequestDto replyToCommentDto = createReplyToCommentRequestDto("Updated content");
        replyToCommentDto.setId(replyId);

        ReplyToComment existingReply = createReplyToComment("Old content");
        existingReply.setId(replyId);

        when(replyToCommentRepo.findById(replyId)).thenReturn(Optional.of(existingReply));

        assertThrows(UnauthorizedReplyUpdateException.class, () ->
                replyToCommentService.update(replyToCommentDto, unauthorizedAuthorId));
    }

    @Test
    void deleteById_ValidIdAndAuthorizedUser_DeletesReply() {
        Long replyToCommentId = 1L;
        Long authorId = 1L;
        ReplyToComment replyToComment = createReplyToComment("content");
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
        ReplyToComment replyToComment = createReplyToComment("content");
        replyToComment.setId(replyToCommentId);

        when(replyToCommentRepo.findById(replyToCommentId)).thenReturn(Optional.of(replyToComment));

        assertThrows(UnauthorizedReplyDeleteException.class, () ->
                replyToCommentService.deleteById(replyToCommentId, unauthorizedAuthorId));
    }

    @Test
    void findAllByCommentId_ValidCommentId_ReturnsReplyToCommentDtos() {
        Long commentId = 1L;
        ReplyToComment replyToComment = createReplyToComment("Test reply");
        replyToComment.setId(1L);

        ReplyToCommentResponseDto replyToCommentDto = new ReplyToCommentResponseDto();
        replyToCommentDto.setContent("Test reply");
        replyToCommentDto.setId(1L);

        List<ReplyToComment> replyToComments = List.of(replyToComment);
        List<ReplyToCommentResponseDto> replyToCommentDtos = List.of(replyToCommentDto);

        when(replyToCommentRepo.findAllByCommentId(commentId)).thenReturn(replyToComments);
        when(responseMapper.toDto(replyToComment)).thenReturn(replyToCommentDto);

        List<ReplyToCommentResponseDto> result = replyToCommentService.findAllByCommentId(commentId);

        assertEquals(replyToCommentDtos, result);
    }

    @Test
    void findAllByCommentId_InvalidCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                replyToCommentService.findAllByCommentId(-1L));
    }

    @Test
    void findAllByCommentId_NullCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                replyToCommentService.findAllByCommentId(null));
    }
}
