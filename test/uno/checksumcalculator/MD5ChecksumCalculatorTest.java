package uno.checksumcalculator;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MD5ChecksumCalculatorTest {
    private static final String MD = "MD5";

    @Test
    public void testMD5ChecksumCalculatorReturnsRightChecksumCalculator() throws NoSuchAlgorithmException {
        MessageDigest expectedDigest = MessageDigest.getInstance(MD);

        MessageDigest actualDigest = new MD5ChecksumCalculator().getHashAlgorithm();

        assertEquals(expectedDigest.getAlgorithm(), actualDigest.getAlgorithm(),
                "Expected MD5 algorithm but found: " + actualDigest.getAlgorithm());
    }

}
