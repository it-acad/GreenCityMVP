package greencity.service;

import greencity.ModelUtils;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Comment;
import greencity.entity.User;
import greencity.exception.exceptions.*;
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
public class CommentServiceImplTest {

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CommentReturnMapper responseMapper;

    @Mock
    private CommentDtoMapper requestMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    public void setUp() {
        comment = createComment("content");
        commentDto = createCommentDto("content");
    }

    private Comment createComment(String content) {
        Comment comment = new Comment();
        comment.setText(content);
        comment.setUser(ModelUtils.getUser());
        return comment;
    }

    private CommentDto createCommentDto(String content) {
        CommentDto dto = new CommentDto();
        dto.setText(content);
        return dto;
    }
    @Test
    void save_ValidData_ReturnsSavedComment() {
        Long commentId = 1L;
        Long authorId = 1L;

        Comment parentComment = new Comment();
        CommentReturnDto savedCommentDto = new CommentReturnDto();
        savedCommentDto.setText("content");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(userRepo.findById(authorId)).thenReturn(Optional.of(comment.getUser()));
        when(requestMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepo.save(comment)).thenReturn(comment);
        when(responseMapper.toDto(comment)).thenReturn(savedCommentDto);

        CommentReturnDto result = commentService.save(commentDto, commentId, authorId);

        assertEquals(savedCommentDto, result);
        verify(commentRepo).save(comment);
    }

    @Test
    void save_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.save(commentDto, invalidCommentId, authorId));
        verify(commentRepo).findById(invalidCommentId);
    }

    @Test
    void save_InvalidAuthorId_ThrowsUserNotFoundException() {
        Long commentId = 1L;
        Long invalidAuthorId = 1L;

        Comment parentComment = new Comment();
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));

        assertThrows(UserNotFoundException.class, () ->
                commentService.save(commentDto, commentId, invalidAuthorId));
        verify(commentRepo).findById(commentId);
    }

    @Test
    void save_InvalidContent_ThrowsContentContainsInvalidCharactersException() {
        Long commentId = 1L;
        Long authorId = 1L;
        CommentDto invalidCommentDto = createCommentDto("Invalid content!");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(new Comment()));
        when(userRepo.findById(authorId)).thenReturn(Optional.of(new User()));

        assertThrows(ContentContainsInvalidCharactersException.class, () ->
                commentService.save(invalidCommentDto, commentId, authorId));
    }

    @Test
    void update_ValidData_ReturnsUpdatedComment() {
        Long commentId = 1L;
        Long authorId = 1L;

        CommentDto updatedDto = createCommentDto("Updated content");

        Comment existingComment = createComment("Old content");
        existingComment.setId(commentId);

        Comment updatedComment = createComment("Updated content");
        updatedComment.setId(commentId);

        CommentReturnDto updatedCommentDto = new CommentReturnDto();
        updatedCommentDto.setText("Updated content");
        updatedCommentDto.setId(commentId);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(responseMapper.toDto(updatedComment)).thenReturn(updatedCommentDto);
        when(commentRepo.save(any(Comment.class))).thenReturn(updatedComment);

        CommentReturnDto result = commentService.update(updatedDto, commentId, authorId);

        assertEquals(updatedCommentDto, result);
        verify(commentRepo).save(updatedComment);
    }

    @Test
    void update_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;
        CommentDto updatedDto = createCommentDto("Updated content");

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.update(updatedDto, invalidCommentId, authorId));

        verify(commentRepo).findById(invalidCommentId);
        verifyNoInteractions(responseMapper);
    }

    @Test
    void update_InvalidContent_ThrowsContentContainsInvalidCharactersException() {
        Long commentId = 1L;
        Long authorId = 1L;
        CommentDto invalidCommentDto = createCommentDto("Invalid content!");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(new Comment()));

        assertThrows(ContentContainsInvalidCharactersException.class, () ->
                commentService.update(invalidCommentDto, commentId, authorId));
    }

    @Test
    void deleteById_ValidIdAndAuthorizedUser_DeletesComment() {
        Long commentId = 1L;
        Long authorId = 1L;
        Comment comment = createComment("content");
        comment.setId(commentId);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteById(commentId, authorId);

        verify(commentRepo).deleteById(commentId);
    }

    @Test
    void deleteById_InvalidId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.deleteById(invalidCommentId, authorId));
        verify(commentRepo).findById(invalidCommentId);
    }

    @Test
    void findAllByCommentId_ValidCommentId_ReturnsCommentDtos() {
        Long commentId = 1L;

        Comment parentComment = new Comment();
        parentComment.setId(commentId);

        Comment replyComment = createComment("Test reply");
        replyComment.setId(1L);

        CommentReturnDto replyCommentDto = new CommentReturnDto();
        replyCommentDto.setText("Test reply");
        replyCommentDto.setId(1L);

        List<Comment> replyComments = List.of(replyComment);
        List<CommentReturnDto> replyCommentDtos = List.of(replyCommentDto);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(commentRepo.findAllByCommentId(commentId)).thenReturn(replyComments);
        when(responseMapper.toDto(replyComment)).thenReturn(replyCommentDto);

        List<CommentReturnDto> result = commentService.findAllByCommentId(commentId);

        assertEquals(replyCommentDtos, result);

        verify(commentRepo).findById(commentId);
        verify(commentRepo).findAllByCommentId(commentId);
    }

    @Test
    void findAllByCommentId_InvalidCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                commentService.findAllByCommentId(-1L));
        verify(commentRepo, never()).findAllByCommentId(any());
    }

    @Test
    void findAllByCommentId_NullCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                commentService.findAllByCommentId(null));
        verify(commentRepo, never()).findAllByCommentId(any());
    }
}
