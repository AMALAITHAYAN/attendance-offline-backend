package com.smartattendance.dto;

import com.smartattendance.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CloseSessionResponse {
    private String sessionId;
    private SessionStatus status;
    private LocalDateTime endTime;
}
