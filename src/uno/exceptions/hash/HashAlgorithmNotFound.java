package uno.exceptions.hash;

public class HashAlgorithmNotFound extends RuntimeException {
    public HashAlgorithmNotFound(String message) {
        super(message);
    }

    public HashAlgorithmNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
