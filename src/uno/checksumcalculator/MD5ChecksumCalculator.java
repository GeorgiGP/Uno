package uno.checksumcalculator;

import uno.exceptions.hash.HashAlgorithmNotFound;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5ChecksumCalculator extends ChecksumCalculator {

    @Override
    protected MessageDigest getHashAlgorithm() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new HashAlgorithmNotFound("MD5 algorithm not found", e);
        }
    }
}
