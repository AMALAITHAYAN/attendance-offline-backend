package com.smartattendance.util;

import java.time.Instant;

public final class TimeWindowUtil {

    private TimeWindowUtil() {}

    public static long windowTime(Instant instant, int tokenWindowSeconds) {
        long epochSeconds = instant.getEpochSecond();
        return epochSeconds / Math.max(1, tokenWindowSeconds);
    }
}
