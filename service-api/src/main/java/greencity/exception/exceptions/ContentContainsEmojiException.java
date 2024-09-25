package greencity.exception.exceptions;

public class ContentContainsEmojiException extends RuntimeException {
    public ContentContainsEmojiException(String message) {
        super(message);
    }
}
