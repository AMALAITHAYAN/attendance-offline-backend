package com.smartattendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @Column(length = 64)
    private String sessionId;

    @Column(nullable = false, length = 128)
    private String sessionSecret;

    // Teacher-configurable
    private Integer qrRefreshIntervalSeconds;  // e.g., 20
    private Integer tokenWindowSeconds;       // e.g., 20
    private Double allowedRadiusMeters;       // e.g., 50
    private Integer durationMinutes;          // e.g., 10

    // Optional geofence anchor
    private Double teacherLat;
    private Double teacherLng;

    // Optional accuracy/age constraints
    private Double maxGpsAccuracyMeters;      // e.g., 50
    private Integer locationMaxAgeSeconds;    // e.g., 30

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}
