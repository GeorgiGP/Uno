package uno.exceptions;

public class NameTakenException extends RuntimeException {
    public NameTakenException(String message) {
        super(message);
    }

    public NameTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
