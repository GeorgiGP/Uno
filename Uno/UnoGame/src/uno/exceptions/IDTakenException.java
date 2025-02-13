package uno.exceptions;

public class IDTakenException extends RuntimeException {
    public IDTakenException(String message) {
        super(message);
    }

    public IDTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
