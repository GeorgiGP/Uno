package uno.exceptions;

public class IDNotExistException extends RuntimeException {
    public IDNotExistException(String message) {
        super(message);
    }

    public IDNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
