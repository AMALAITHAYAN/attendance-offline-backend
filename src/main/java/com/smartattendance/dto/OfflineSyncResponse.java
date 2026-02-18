package com.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OfflineSyncResponse {
    private int accepted;
    private int rejected;
    private List<OfflineSyncItemResult> results;
}
