package Model;

/**
 * Signals a problem while reading or saving the events file. The message is
 * already written in a form suitable for showing to the user.
 */
public class StorageException extends Exception {
    /**
     * Creates the exception with a user-facing message.
     *
     * @param message the message to display
     */
    public StorageException(String message) {
        super(message);
    }
}
