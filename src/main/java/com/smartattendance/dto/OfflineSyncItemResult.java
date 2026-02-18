package com.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OfflineSyncItemResult {
    private String studentId;
    private String status;   // ACCEPTED / REJECTED
    private String message;  // reason
}
