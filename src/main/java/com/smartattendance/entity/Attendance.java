package com.smartattendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_attendance_session_student", columnNames = {"session_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_attendance_session", columnList = "session_id"),
                @Index(name = "idx_attendance_student", columnList = "student_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 64)
    private String studentId;

    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    // Token fields used in offline proof
    private Long windowTime;      // floor(epochSeconds / tokenWindow)

    @Column(length = 128)
    private String token;

    @Column(length = 128)
    private String proof;

    private Integer confidenceScore;

    @Column(length = 255)
    private String userAgent;

    @Column(length = 32)
    private String screenResolution;

    @Column(length = 64)
    private String deviceId;

    private Double studentLat;
    private Double studentLng;
    private Double gpsAccuracyMeters;
    private Double distanceMeters;

    private LocalDateTime verifiedAt;
}
