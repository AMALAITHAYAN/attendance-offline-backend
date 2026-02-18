package com.smartattendance.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionRequest {

    @NotNull
    @Min(5)
    @Max(120)
    private Integer qrRefreshIntervalSeconds;

    @NotNull
    @Min(5)
    @Max(120)
    private Integer tokenWindowSeconds;

    @NotNull
    @DecimalMin("5")
    @DecimalMax("500")
    private Double allowedRadiusMeters;

    @NotNull
    @Min(1)
    @Max(180)
    private Integer durationMinutes;

    // Optional
    private Double teacherLat;
    private Double teacherLng;

    // Optional (recommended defaults: 50m accuracy, 30s age)
    @DecimalMin("5")
    @DecimalMax("200")
    private Double maxGpsAccuracyMeters;

    @Min(5)
    @Max(300)
    private Integer locationMaxAgeSeconds;
}
