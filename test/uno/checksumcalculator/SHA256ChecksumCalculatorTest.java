package uno.checksumcalculator;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SHA256ChecksumCalculatorTest {
    private static final String SHA = "SHA-256";

    @Test
    public void testMD5ChecksumCalculatorReturnsRightChecksumCalculator() throws NoSuchAlgorithmException {
        MessageDigest expectedDigest = MessageDigest.getInstance(SHA);

        MessageDigest actualDigest = new SHA256ChecksumCalculator().getHashAlgorithm();

        assertEquals(expectedDigest.getAlgorithm(), actualDigest.getAlgorithm(),
                "Expected SHA256 algorithm but found: " + actualDigest.getAlgorithm());
    }
}
