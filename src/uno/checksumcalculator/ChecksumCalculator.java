package uno.checksumcalculator;

import org.jetbrains.annotations.NotNull;
import uno.exceptions.hash.HashAlgorithmNotFound;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class ChecksumCalculator {
    private static final int MAX_BUFFER_SIZE = 4096;

    public static ChecksumCalculator of(@NotNull String type) {
        return switch (type.toLowerCase()) {
            case "md5" -> new MD5ChecksumCalculator();
            case "sha256" -> new SHA256ChecksumCalculator();
            default -> throw new IllegalArgumentException("Invalid checksum type: " + type);
        };
    }

    public String calculate(@NotNull InputStream stream) {
        try {
            MessageDigest hashAlgorithm = getHashAlgorithm();
            return calculate(hashAlgorithm, stream);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to get hash algorithm", e);
        } catch (NoSuchAlgorithmException e) {
            throw new HashAlgorithmNotFound("Failed to get hash algorithm", e);
        }
    }

    // Template method
    protected abstract MessageDigest getHashAlgorithm() throws NoSuchAlgorithmException;

    private String calculate(@NotNull MessageDigest hashAlgorithm, @NotNull InputStream stream) throws IOException {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            hashAlgorithm.update(buffer, 0, bytesRead);
        }
        byte[] hashBytes = hashAlgorithm.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : hashBytes) {
            result.append(String.format("%02x", b));
            //ensures that per byte there are always 2 hex digits: 15 -> 0f, 16 -> 10
        }
        return result.toString();
    }
}
