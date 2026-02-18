package com.smartattendance.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public final class RandomUtil {

    private static final SecureRandom RAND = new SecureRandom();

    private RandomUtil() {}

    public static String newSessionId() {
        // Short, human-friendly
        return "SES-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    public static String newSecret() {
        byte[] bytes = new byte[32];
        RAND.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
