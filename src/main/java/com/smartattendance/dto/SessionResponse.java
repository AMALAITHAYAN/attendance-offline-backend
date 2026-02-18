package com.smartattendance.dto;

import com.smartattendance.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class SessionResponse {
    private String sessionId;

    // Returned only for teacher (start session)
    private String sessionSecret;

    private Integer qrRefreshIntervalSeconds;
    private Integer tokenWindowSeconds;
    private Double allowedRadiusMeters;
    private Integer durationMinutes;

    private Double teacherLat;
    private Double teacherLng;

    private Double maxGpsAccuracyMeters;
    private Integer locationMaxAgeSeconds;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
}
