package greencity.exception.exceptions;

/**
 * Exception that is thrown when a user has already joined the event.
 *
 * @author [vulook].
 */
public class EventAlreadyJoinedException extends RuntimeException {

    /**
     * Constructor for EventAlreadyJoinedExceptionn.
     *
     * @param message - giving message.
     */
    public EventAlreadyJoinedException(String message) {
        super(message);
    }
}