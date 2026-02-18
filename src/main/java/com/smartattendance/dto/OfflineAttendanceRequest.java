package com.smartattendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfflineAttendanceRequest {

    @NotBlank
    private String studentId;

    @NotBlank
    private String sessionId;

    @NotNull
    private Long windowTime;

    @NotBlank
    private String token;

    @NotBlank
    private String proof;

    private Integer confidenceScore;

    private String userAgent;
    private String screenResolution;
    private String deviceId;

    private Double studentLat;
    private Double studentLng;
    private Double gpsAccuracyMeters;

    @NotNull
    private LocalDateTime verifiedAt;

    // Optional: location captured time (for max-age validation)
    private LocalDateTime locationCapturedAt;
}
