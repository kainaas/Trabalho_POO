package Model;

/**
 * Thrown when the data informed for an event ara invalid.
 * The message already comes read to be shown to the user.
 */
public class EventValidationException extends Exception {
    public EventValidationException(String message) {
        super(message);
    }
}
