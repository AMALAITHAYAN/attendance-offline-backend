package com.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AttendanceSummaryDTO {
    private String studentId;
    private Integer confidenceScore;
    private String deviceId;
    private String screenResolution;
    private LocalDateTime verifiedAt;
    private Double distanceMeters;
}
