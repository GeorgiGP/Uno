package uno.checksumcalculator;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChecksumCalculatorTest {
    private final byte[] array = {0x00, 0x0f, 0x10, (byte)0xff};

    public class DerivedTest extends ChecksumCalculator {
        @Override
        protected MessageDigest getHashAlgorithm() {
            MessageDigest mockMessageDigest = mock();
            when(mockMessageDigest.digest()).thenReturn(array);
            return mockMessageDigest;
        }
    }

    @Test
    public void testBaseChecksumCalculator() {
        //we don't want to test MessageDigest so mocked and testing only the logic with hex bytes appending in string
        ChecksumCalculator calculator = new DerivedTest();

        InputStream is = new ByteArrayInputStream("".getBytes());
        String result = "000f10ff";
        assertEquals(result, calculator.calculate(is),
                "when each byte is: " + Arrays.toString(array) +
                        " result string from calculate should be in hex: " + result);
    }

    @Test
    void testCreateChecksumCalculatorMD5() {
        String md = "mD5";
        assertEquals(MD5ChecksumCalculator.class, ChecksumCalculator.of(md).getClass(),
                "After requesting for: " + md + " should return MD5ChecksumCalculator");
    }

    @Test
    void testCreateChecksumCalculatorSHA256() {
        String sha = "SHa256";
        assertEquals(SHA256ChecksumCalculator.class, ChecksumCalculator.of(sha).getClass(),
                "After requesting for: " + sha + " should return Sha256ChecksumCalculator");
    }

    @Test
    void testCreateChecksumCalculatorOther() {
        assertThrows(IllegalArgumentException.class, () -> ChecksumCalculator.of("unknown"),
                "After requesting for: " + null + " should throw IllegalArgumentException");
    }
}
