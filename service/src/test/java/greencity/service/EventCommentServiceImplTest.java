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
public class EventCommentServiceImplTest {

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
        this.comment = createComment("content");
        this.commentDto = createCommentDto("content");
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

        when(this.commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(this.userRepo.findById(authorId)).thenReturn(Optional.of(this.comment.getAuthor()));
        when(this.eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(this.requestMapper.toEntity(commentDto)).thenReturn(this.comment);
        when(this.commentRepo.save(this.comment)).thenReturn(this.comment);
        when(this.responseMapper.toDto(this.comment)).thenReturn(savedCommentDto);

        EventCommentDtoResponse result = this.commentService.saveReply(this.commentDto, commentId, authorId, eventId);

        assertEquals(savedCommentDto, result);
        verify(this.commentRepo).save(this.comment);
    }

    @Test
    void save_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long eventId = 1L;
        Long authorId = 1L;

        when(this.commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                this.commentService.saveReply(this.commentDto, invalidCommentId, authorId, eventId));
        verify(this.commentRepo).findById(invalidCommentId);
    }

    @Test
    void save_InvalidAuthorId_ThrowsUserNotFoundException() {
        Long commentId = 1L;
        Long eventId = 1L;
        Long invalidAuthorId = 1L;

        EventComment parentComment = new EventComment();
        when(this.commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));

        assertThrows(UserNotFoundException.class, () ->
                this.commentService.saveReply(this.commentDto, commentId, invalidAuthorId, eventId));
        verify(this.commentRepo).findById(commentId);
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

        when(this.commentRepo.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(this.responseMapper.toDto(updatedComment)).thenReturn(updatedCommentDto);
        when(this.commentRepo.save(any(EventComment.class))).thenReturn(updatedComment);

        EventCommentDtoResponse result = this.commentService.updateReply(updatedDto, commentId, authorId);

        assertEquals(updatedCommentDto, result);
        verify(this.commentRepo).save(updatedComment);
    }

    @Test
    void update_InvalidCommentId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;
        EventCommentDtoRequest updatedDto = createCommentDto("Updated content");

        when(this.commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                this.commentService.updateReply(updatedDto, invalidCommentId, authorId));

        verify(this.commentRepo).findById(invalidCommentId);
        verifyNoInteractions(this.responseMapper);
    }

    @Test
    void deleteById_ValidIdAndAuthorizedUser_DeletesComment() {
        Long commentId = 1L;
        Long authorId = 1L;
        EventComment comment = createComment("content");
        comment.setId(commentId);

        when(this.commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        this.commentService.deleteReplyById(commentId, authorId);

        verify(this.commentRepo).deleteById(commentId);
    }

    @Test
    void deleteById_InvalidId_ThrowsCommentNotFoundException() {
        Long invalidCommentId = 1L;
        Long authorId = 1L;

        when(this.commentRepo.findById(invalidCommentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                this.commentService.deleteReplyById(invalidCommentId, authorId));
        verify(this.commentRepo).findById(invalidCommentId);
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

        when(this.commentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(this.commentRepo.findAllByEventCommentId(commentId)).thenReturn(replyComments);
        when(this.responseMapper.toDto(replyComment)).thenReturn(replyCommentDto);

        List<EventCommentDtoResponse> result = commentService.findAllReplyByCommentId(commentId);

        assertEquals(replyCommentDtos, result);

        verify(this.commentRepo).findById(commentId);
        verify(this.commentRepo).findAllByEventCommentId(commentId);
    }

    @Test
    void findAllByCommentId_InvalidCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                this.commentService.findAllReplyByCommentId(-1L));
        verify(this.commentRepo, never()).findAllByEventCommentId(any());
    }

    @Test
    void findAllByCommentId_NullCommentId_ThrowsInvalidCommentIdException() {
        assertThrows(InvalidCommentIdException.class, () ->
                this.commentService.findAllReplyByCommentId(null));
        verify(this.commentRepo, never()).findAllByEventCommentId(any());
    }
}
