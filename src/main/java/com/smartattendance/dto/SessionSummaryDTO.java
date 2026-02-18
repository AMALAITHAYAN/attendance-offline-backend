package com.smartattendance.dto;

import com.smartattendance.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class SessionSummaryDTO {
    private String sessionId;
    private SessionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long totalAttendance;
    private Double avgConfidenceScore;
}
