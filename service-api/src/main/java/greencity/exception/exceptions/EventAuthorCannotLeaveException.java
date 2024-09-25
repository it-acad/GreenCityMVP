package greencity.exception.exceptions;

/**
 * Exception that is thrown when an author attempts to leave their own event.
 *
 * @author [vulook].
 */
public class EventAuthorCannotLeaveException extends RuntimeException {

    /**
     * Constructor for AuthorCannotLeaveEventException.
     *
     * @param message - giving message.
     */
    public EventAuthorCannotLeaveException(String message) {
        super(message);
    }
}