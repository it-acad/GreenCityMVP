package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.client.RestClient;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.CommentNotFoundException;
import greencity.exception.exceptions.InvalidCommentIdException;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.mapping.EventCommentDtoRequestMapper;
import greencity.mapping.EventCommentResponseMapper;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class EventCommentServiceImpl implements EventCommentService{
    private final UserRepo userRepo;
@AllArgsConstructor
@Transactional
public class EventCommentServiceImpl implements EventCommentService {
    private final EventCommentRepo eventCommentRepo;
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final HttpServletRequest httpServletRequest;
    private final RestClient restClient;
    private ModelMapper modelMapper;

    static Map<String, String[]> words = new HashMap<>();
    static int largestWordLength = 0;

    static {
        loadConfigs();
    }
    private final EventCommentResponseMapper responseMapper;
    private final EventCommentDtoRequestMapper requestMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventCommentServiceImpl.class.getName());

    @Override
    public AddEventCommentDtoResponse addComment(Long eventId, AddEventCommentDtoRequest commentDto
            , UserVO currentUserVO) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        String filteredText = filterText(commentDto.getText(), currentUserVO.getName());
        if (filteredText.equals("This comment were blocked because you were using swear words")) {
            throw new IllegalArgumentException(filteredText);
        }

        User currentUser = modelMapper.map(currentUserVO, User.class);

        EventComment comment = EventComment.builder()
                .text(filteredText)
                .user(currentUser)
                .event(event)
                .mentionedUsers(getMentionedUsers(commentDto.getText()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    public EventCommentDtoResponse saveReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId) {
        EventComment parentComment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        User user = userRepo.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));
        EventComment comment = requestMapper.toEntity(commentDtoRequest);
        comment.setAuthor(user);
        comment.setParentComment(parentComment);
        EventComment savedComment = eventCommentRepo.save(comment);

        //send notification to organizer(User author)
        sendNotificationToOrganizer(event , savedComment);

        return modelMapper.map(savedComment, AddEventCommentDtoResponse.class);
        return responseMapper.toDto(savedComment);
    }

    @Override
    public List<AddEventCommentDtoResponse> getCommentsByEventId(Long eventId) {
        List<EventComment> comments = eventCommentRepo.findByEventIdOrderByCreatedAtDesc(eventId);
    @PreAuthorize("hasRole('ADMIN') or @eventCommentServiceImpl.isOwner(#commentId, #authorId)")
    public EventCommentDtoResponse updateReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId) {
        EventComment existingComment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        return comments.stream()
                .map(c -> modelMapper.map(c, AddEventCommentDtoResponse.class))
                .collect(Collectors.toList());
        existingComment.setContent(commentDtoRequest.getText());
        existingComment.setIsEdited(true);
        EventComment updatedComment = eventCommentRepo.save(existingComment);
        return responseMapper.toDto(updatedComment);
    }

    @Override
    public Long showQuantityOfAddedComments(Long eventId) {
        if (!eventRepo.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
        return eventCommentRepo.countByEventId(eventId);
    @PreAuthorize("hasRole('ADMIN') or @eventCommentServiceImpl.isOwner(#commentId, #authorId)")
    public void deleteReplyById(Long commentId, Long authorId) {
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        eventCommentRepo.deleteById(commentId);
    }

    @Override
    public AddEventCommentDtoResponse replyToComment(Long eventId, Long parentCommentId
            , AddEventCommentDtoRequest replyDto, UserVO currentUserVO) {
        EventComment parentComment = eventCommentRepo.findById(parentCommentId)
                .orElseThrow(() -> new EventNotFoundException("Parent comment not found"));

        User currentUser = modelMapper.map(currentUserVO, User.class);

        EventComment reply = EventComment.builder()
                .text(replyDto.getText())
                .user(currentUser)
                .event(parentComment.getEvent())
                .parentComment(parentComment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EventComment savedReply = eventCommentRepo.save(reply);

        return modelMapper.map(savedReply, AddEventCommentDtoResponse.class);
    }
    public List<EventCommentDtoResponse> findAllReplyByCommentId(Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        if (commentId == null || commentId < 0) {
            throw new InvalidCommentIdException(ErrorMessage.INVALID_COMMENT_ID + commentId);
        }

    public void sendNotificationToOrganizer(Event event , EventComment eventComment) {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);

        PlaceAuthorDto placeAuthorDto = modelMapper.map(event.getAuthor(), PlaceAuthorDto.class);
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));

        //build dto object
        EventCommentNotificationDto commentNotificationDto = EventCommentNotificationDto.builder()
                .eventTitle(event.getEventTitle())
                .commentText(eventComment.getText())
                .commentAuthor(eventComment.getUser().getName())
                .author(placeAuthorDto)
                .secureToken(accessToken)
                .commentDate(eventComment.getCreatedAt().toString())
                .build();
        List<EventComment> replies = eventCommentRepo.findAllByEventCommentId(commentId);

        //send comment details
        restClient.sendEventCommentNotification(commentNotificationDto);
        return replies.stream()
                .map(responseMapper::toDto)
                .toList();
    }

    private List<User> getMentionedUsers(String text) {
        List<User> mentionedUsers = new ArrayList<>();
        StringBuilder invalidMentions = new StringBuilder();

        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
        Matcher matcher = mentionPattern.matcher(text);

        while (matcher.find()) {
            String userName = matcher.group(1);

            Optional<User> mentionedUser = userRepo.findByName(userName);

            if (mentionedUser.isPresent()) {
                mentionedUsers.add(mentionedUser.get());
            } else {
                invalidMentions.append("@").append(userName).append(" ");
            }
        }
        if (!invalidMentions.isEmpty()) {
            throw new IllegalArgumentException("The following mentions are invalid: "
                    + invalidMentions.toString().trim());
        }
        return mentionedUsers;
    }

    //load configuration for bad words
    private static void loadConfigs() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv").openConnection().getInputStream()));
            String line = "";
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                counter++;
                String[] content = null;
                try {
                    content = line.split(",");
                    if (content.length == 0) {
                        continue;
                    }
                    String word = content[0];
                    String[] ignore_in_combination_with_words = new String[]{};
                    if (content.length > 1) {
                        ignore_in_combination_with_words = content[1].split("_");
                    }

                    if (word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }
                    words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Loaded " + counter + " words to filter out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check for bad words
    private static ArrayList<String> badWordsFound(String input) {
        if (input == null) {
            return new ArrayList<>();
        }
        input = input.replaceAll("1", "i")
                .replaceAll("!", "i")
                .replaceAll("3", "e")
                .replaceAll("4", "a")
                .replaceAll("@", "a")
                .replaceAll("5", "s")
                .replaceAll("7", "t")
                .replaceAll("0", "o")
                .replaceAll("9", "g");

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        for (int start = 0; start < input.length(); start++) {
            for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
                String wordToCheck = input.substring(start, start + offset);
                if (words.containsKey(wordToCheck)) {
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for (String ignoreWord : ignoreCheck) {
                        if (input.contains(ignoreWord)) {
                            ignore = true;
                            break;
                        }
                    }
                    if (!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }
        return badWords;
    }

    //filtering text for bad words
    @Override
    public String filterText(String input, String userName) {
        ArrayList<String> badWords = badWordsFound(input);

        if (!badWords.isEmpty()) {
            return "This comment were blocked because you were using swear words";
        }
        return input;
    public boolean isOwner(Long commentId, Long userId) {
        EventComment comment = eventCommentRepo.findById(commentId).orElse(null);
        return comment != null && comment.getAuthor().getId().equals(userId);
    }
}
