package com.smartattendance.util;

public final class TokenGeneratorUtil {

    private TokenGeneratorUtil() {}

    /**
     * Token is derived from secret so backend/teacher can verify authenticity.
     * Format is stable & deterministic.
     */
    public static String generateToken(String sessionId, long windowTime, String sessionSecret) {
        String payload = sessionId + ":" + windowTime + ":" + sessionSecret;
        return HashUtil.sha256Hex(payload);
    }
}
