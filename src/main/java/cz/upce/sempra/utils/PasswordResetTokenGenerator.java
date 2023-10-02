package cz.upce.sempra.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordResetTokenGenerator {
    public static String generateResetToken() {
        int tokenLength = 32;
        byte[] tokenBytes = new byte[tokenLength];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}