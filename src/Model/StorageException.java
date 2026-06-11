package Model;

/**
 * Problem while reading or saving the event files.
 * the message comes ready to be shown to the user.
 */
public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }
}
