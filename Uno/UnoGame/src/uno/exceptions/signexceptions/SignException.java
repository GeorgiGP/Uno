package uno.exceptions.signexceptions;

public class SignException extends Exception {
    public SignException(String message) {
        super(message);
    }

    public SignException(String message, Throwable cause) {
        super(message, cause);
    }
}
