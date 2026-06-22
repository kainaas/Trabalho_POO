package Model;

/**
 * Thrown when the data supplied for an event is invalid. The message is already
 * written in a form suitable for showing to the user.
 */
public class EventValidationException extends Exception {
    /**
     * Creates the exception with a user-facing message.
     *
     * @param message the message to display
     */
    public EventValidationException(String message) {
        super(message);
    }
}
