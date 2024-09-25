package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentSendEmailDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.EventCommentNotFoundException;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
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
import org.modelmapper.ModelMapper;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCommentServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private EventCommentRepo commentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private RestClient restClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentServiceImpl eventCommentServiceImpl;

    private Long eventId;
    private Long commentId;
    private AddEventCommentDtoRequest commentDto;
    private UserVO userVO;
    private Event event;
    private User user;
    private EventComment eventComment;
    private AddEventCommentDtoResponse responseDto;
    private PlaceAuthorDto placeAuthorDto;

    @BeforeEach
    public void setup() {
        eventId = 1L;
        commentId = 1L;
        commentDto = new AddEventCommentDtoRequest();
        commentDto.setText("Comment text");

        userVO = new UserVO();
        user = new User();
        event = new Event();
        event.setId(eventId);
        event.setAuthor(user);

        eventComment = EventComment.builder()
                .text("Comment text")
                .user(user)
                .event(event)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        responseDto = new AddEventCommentDtoResponse();
        responseDto.setEventId(eventId);
        responseDto.setText("Comment text");

        placeAuthorDto = new PlaceAuthorDto();

        try {
            Method loadConfigsMethod = EventCommentServiceImpl.class.getDeclaredMethod("loadConfigs");
            loadConfigsMethod.setAccessible(true);
            loadConfigsMethod.invoke(null);
        } catch (Exception e) {
            fail("Failed to invoke loadConfigs method: " + e.getMessage());
        }
    }

    private void mockCommonEventMethods() {
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventCommentRepo.save(any(EventComment.class))).thenReturn(eventComment);
        when(modelMapper.map(eventComment, AddEventCommentDtoResponse.class)).thenReturn(responseDto);
        when(modelMapper.map(event.getAuthor(), PlaceAuthorDto.class)).thenReturn(placeAuthorDto);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer test-token");
    }

    @Test
    void addComment() {
        mockCommonEventMethods();

        AddEventCommentDtoResponse result = eventCommentServiceImpl.addComment(eventId, commentDto, userVO);

        assertNotNull(result);
        assertEquals("Comment text", result.getText());
        assertEquals(eventId, result.getEventId());
        verify(eventCommentRepo, times(1)).save(any(EventComment.class));
    }

    @Test
    void showQuantityOfAddedComments() {
        Long expectedCount = 5L;

        when(eventRepo.existsById(eventId)).thenReturn(true);
        when(eventCommentRepo.countByEventId(eventId)).thenReturn(expectedCount);

        Long result = eventCommentServiceImpl.showQuantityOfAddedComments(eventId);

        assertNotNull(result);
        assertEquals(expectedCount, result);
        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(1)).countByEventId(eventId);
    }

    @Test
    public void showQuantityOfAddedComments_EventNotFound() {
        when(eventRepo.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            eventCommentServiceImpl.showQuantityOfAddedComments(eventId);
        });

        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(0)).countByEventId(anyLong());
    }

    @Test
    public void getCommentById_Success() {
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.of(eventComment));
        when(modelMapper.map(eventComment, AddEventCommentDtoResponse.class)).thenReturn(responseDto);

        AddEventCommentDtoResponse result = eventCommentServiceImpl.getCommentById(commentId);

        assertNotNull(result);
        verify(eventCommentRepo, times(1)).findById(commentId);
        verify(modelMapper, times(1)).map(eventComment, AddEventCommentDtoResponse.class);
    }

    @Test
    public void getCommentById_CommentNotFound() {
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EventCommentNotFoundException.class, () -> {
            eventCommentServiceImpl.getCommentById(commentId);
        });

        verify(eventCommentRepo, times(1)).findById(commentId);
        verify(modelMapper, times(0)).map(any(EventComment.class), eq(AddEventCommentDtoResponse.class));
    }

    @Test
    public void getCommentsByEventId() {
        when(eventRepo.existsById(eventId)).thenReturn(true);
        when(eventCommentRepo.findByEventIdOrderByCreatedAtDesc(eventId))
                .thenReturn(Collections.singletonList(eventComment));

        List<AddEventCommentDtoResponse> result = eventCommentServiceImpl.getCommentsByEventId(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Comment text", result.get(0).getText());
        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(1)).findByEventIdOrderByCreatedAtDesc(eventId);
    }

    @Test
    public void getCommentsByEventId_ShouldThrowEventNotFoundException() {
        when(eventRepo.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            eventCommentServiceImpl.getCommentsByEventId(eventId);
        });

        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(0)).findByEventIdOrderByCreatedAtDesc(eventId);
    }

    @Test
    public void addComment_WithNotification_Success() {
        mockCommonEventMethods();

        AddEventCommentDtoResponse result = eventCommentServiceImpl.addComment(eventId, commentDto, userVO);

        assertNotNull(result);
        assertEquals("Comment text", result.getText());
        assertEquals(eventId, result.getEventId());

        verify(restClient, times(1)).sendEventCommentNotification(any(EventCommentSendEmailDto.class));
        verify(eventCommentRepo, times(1)).save(any(EventComment.class));
    }

    @Test
    public void getMentionedUsers() throws Exception {
        String text = "@Nazar and @Oleg";
        User john = new User();
        john.setName("Nazar");
        User jane = new User();
        jane.setName("Oleg");

        when(userRepo.findByName("Nazar")).thenReturn(Optional.of(john));
        when(userRepo.findByName("Oleg")).thenReturn(Optional.of(jane));

        Method method = EventCommentServiceImpl.class.getDeclaredMethod("getMentionedUsers", String.class);
        method.setAccessible(true);
        List<User> result = (List<User>) method.invoke(eventCommentServiceImpl, text);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(john));
        assertTrue(result.contains(jane));
        verify(userRepo, times(1)).findByName("Nazar");
        verify(userRepo, times(1)).findByName("Oleg");
    }

    @Test
    public void filterText_WithBadWords_ShouldReturnBlockedMessage() {
        String inputText = "anita";//this is some bad word
        String userName = "JohnDoe";

        String result = eventCommentServiceImpl.filterText(inputText, userName);

        assertEquals("This comment were blocked because you were using swear words", result);
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
