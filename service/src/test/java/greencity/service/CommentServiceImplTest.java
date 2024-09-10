package greencity.service;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.exception.exceptions.*;
import greencity.mapping.EventCommentDtoRequestMapper;
import greencity.mapping.EventCommentResponseMapper;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
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
    private EventCommentRepo commentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private EventCommentResponseMapper responseMapper;

    @Mock
    private EventCommentDtoRequestMapper requestMapper;

    @InjectMocks
    private EventCommentServiceImpl commentService;

    private EventComment comment;
    private EventCommentDtoRequest commentDto;

    @BeforeEach
    public void setUp() {
        comment = createComment("content");
        commentDto = createCommentDto("content");
    }

    private EventComment createComment(String content) {
        EventComment comment = new EventComment();
        comment.setContent(content);
        comment.setAuthor(ModelUtils.getUser());
        return comment;
    }

    private EventCommentDtoRequest createCommentDto(String content) {
        EventCommentDtoRequest dto = new EventCommentDtoRequest();
        dto.setText(content);
        return dto;
    }
    @Test
    void save_ValidData_ReturnsSavedComment() {
        Long commentId = 1L;
        Long authorId = 1L;
        Long eventId = 1L;

        Event event = new Event();
        event.setId(eventId);

        EventComment parentComment = new EventComment();
        EventCommentDtoResponse savedCommentDto = new EventCommentDtoResponse();
        savedCommentDto.setText("content");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(userRepo.findById(authorId)).thenReturn(Optional.of(comment.getAuthor()));
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(requestMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepo.save(comment)).thenReturn(comment);
        when(responseMapper.toDto(comment)).thenReturn(savedCommentDto);

        EventCommentDtoResponse result = commentService.saveReply(commentDto, commentId, authorId, eventId);

        assertEquals(savedCommentDto, result);
        verify(commentRepo).save(comment);
    }

    @Test
    void save_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long eventId = 1L;
        Long authorId = 1L;

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.saveReply(commentDto, invalidCommentId, authorId, eventId));
        verify(commentRepo).findById(invalidCommentId);
    }

    @Test
    void save_InvalidAuthorId_ThrowsUserNotFoundException() {
        Long commentId = 1L;
        Long eventId = 1L;
        Long invalidAuthorId = 1L;

        EventComment parentComment = new EventComment();
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));

        assertThrows(UserNotFoundException.class, () ->
                commentService.saveReply(commentDto, commentId, invalidAuthorId, eventId));
        verify(commentRepo).findById(commentId);
    }

    @Test
    void update_ValidData_ReturnsUpdatedComment() {
        Long commentId = 1L;
        Long authorId = 1L;


        EventCommentDtoRequest updatedDto = createCommentDto("Updated content");

        EventComment existingComment = createComment("Old content");
        existingComment.setId(commentId);

        EventComment updatedComment = createComment("Updated content");
        updatedComment.setId(commentId);

        EventCommentDtoResponse updatedCommentDto = new EventCommentDtoResponse();
        updatedCommentDto.setText("Updated content");
        updatedCommentDto.setId(commentId);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(responseMapper.toDto(updatedComment)).thenReturn(updatedCommentDto);
        when(commentRepo.save(any(EventComment.class))).thenReturn(updatedComment);

        EventCommentDtoResponse result = commentService.updateReply(updatedDto, commentId, authorId);

        assertEquals(updatedCommentDto, result);
        verify(commentRepo).save(updatedComment);
    }

    @Test
    void update_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;
        EventCommentDtoRequest updatedDto = createCommentDto("Updated content");

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.updateReply(updatedDto, invalidCommentId, authorId));

        verify(commentRepo).findById(invalidCommentId);
        verifyNoInteractions(responseMapper);
    }

    @Test
    void deleteById_ValidIdAndAuthorizedUser_DeletesComment() {
        Long commentId = 1L;
        Long authorId = 1L;
        EventComment comment = createComment("content");
        comment.setId(commentId);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteReplyById(commentId, authorId);

        verify(commentRepo).deleteById(commentId);
    }

    @Test
    void deleteById_InvalidId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;

        when(commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.deleteReplyById(invalidCommentId, authorId));
        verify(commentRepo).findById(invalidCommentId);
    }

    @Test
    void findAllReplyByCommentId_ValidCommentId_ReturnsCommentDtos() {
        Long commentId = 1L;

        EventComment parentComment = new EventComment();
        parentComment.setId(commentId);

        EventComment replyComment = createComment("Test reply");
        replyComment.setId(1L);

        EventCommentDtoResponse replyCommentDto = new EventCommentDtoResponse();
        replyCommentDto.setText("Test reply");

        List<EventComment> replyComments = List.of(replyComment);
        List<EventCommentDtoResponse> replyCommentDtos = List.of(replyCommentDto);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(commentRepo.findAllByEventCommentId(commentId)).thenReturn(replyComments);
        when(responseMapper.toDto(replyComment)).thenReturn(replyCommentDto);

        List<EventCommentDtoResponse> result = commentService.findAllReplyByCommentId(commentId);

        assertEquals(replyCommentDtos, result);

        verify(commentRepo).findById(commentId);
        verify(commentRepo).findAllByEventCommentId(commentId);
    }

    @Test
    void findAllByCommentId_InvalidCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                commentService.findAllReplyByCommentId(-1L));
        verify(commentRepo, never()).findAllByEventCommentId(any());
    }

    @Test
    void findAllByCommentId_NullCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                commentService.findAllReplyByCommentId(null));
        verify(commentRepo, never()).findAllByEventCommentId(any());
    }
}
