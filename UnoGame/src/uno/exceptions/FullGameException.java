package uno.exceptions;

public class FullGameException extends RuntimeException {
    public FullGameException(String message) {
        super(message);
    }

    public FullGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
