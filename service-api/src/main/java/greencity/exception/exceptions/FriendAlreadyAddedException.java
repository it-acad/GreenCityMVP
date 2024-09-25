package greencity.exception.exceptions;

public class FriendAlreadyAddedException extends BadRequestException {

    /**
     * Constructor.
     */
    public FriendAlreadyAddedException(String message) {
        super(message);
    }
}
