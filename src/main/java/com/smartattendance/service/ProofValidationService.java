package com.smartattendance.service;

import com.smartattendance.dto.OfflineAttendanceRequest;
import com.smartattendance.dto.OfflineSyncItemResult;
import com.smartattendance.dto.OfflineSyncResponse;
import com.smartattendance.entity.Attendance;
import com.smartattendance.entity.Session;
import com.smartattendance.entity.SessionStatus;
import com.smartattendance.repository.AttendanceRepository;
import com.smartattendance.util.DistanceUtil;
import com.smartattendance.util.HashUtil;
import com.smartattendance.util.TimeWindowUtil;
import com.smartattendance.util.TokenGeneratorUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProofValidationService {

    private final SessionService sessionService;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    public ProofValidationService(SessionService sessionService,
                                  AttendanceRepository attendanceRepository,
                                  AttendanceService attendanceService) {
        this.sessionService = sessionService;
        this.attendanceRepository = attendanceRepository;
        this.attendanceService = attendanceService;
    }

    /**
     * Validates offline proofs and stores accepted attendance.
     */
    public OfflineSyncResponse validateAndStore(List<OfflineAttendanceRequest> records) {

        int accepted = 0;
        int rejected = 0;

        List<OfflineSyncItemResult> results = new ArrayList<>();

        for (OfflineAttendanceRequest r : records) {

            try {

                ValidationResult vr = validateSingle(r);

                if (!vr.accepted) {

                    rejected++;

                    results.add(
                            OfflineSyncItemResult.builder()
                                    .studentId(r.getStudentId())
                                    .status("REJECTED")
                                    .message(vr.message)
                                    .build()
                    );

                    continue;
                }

                Attendance a = Attendance.builder()
                        .studentId(r.getStudentId())
                        .sessionId(r.getSessionId())
                        .windowTime(r.getWindowTime())
                        .token(r.getToken())
                        .proof(r.getProof())
                        .confidenceScore(r.getConfidenceScore())
                        .userAgent(r.getUserAgent())
                        .screenResolution(r.getScreenResolution())
                        .deviceId(r.getDeviceId())
                        .studentLat(r.getStudentLat())
                        .studentLng(r.getStudentLng())
                        .gpsAccuracyMeters(r.getGpsAccuracyMeters())
                        .distanceMeters(vr.distanceMeters)
                        .verifiedAt(r.getVerifiedAt())
                        .build();

                attendanceService.saveNew(a);

                accepted++;

                results.add(
                        OfflineSyncItemResult.builder()
                                .studentId(r.getStudentId())
                                .status("ACCEPTED")
                                .message("Stored")
                                .build()
                );

            } catch (Exception e) {

                rejected++;

                results.add(
                        OfflineSyncItemResult.builder()
                                .studentId(r.getStudentId())
                                .status("REJECTED")
                                .message(e.getMessage() == null ? "Validation failed" : e.getMessage())
                                .build()
                );
            }
        }

        return OfflineSyncResponse.builder()
                .accepted(accepted)
                .rejected(rejected)
                .results(results)
                .build();
    }

    private ValidationResult validateSingle(OfflineAttendanceRequest r) {

        Session s = sessionService.getSessionEntity(r.getSessionId());

        /* -------------------------------------------------
           Duplicate prevention
        ------------------------------------------------- */
        if (attendanceRepository.existsBySessionIdAndStudentId(
                r.getSessionId(), r.getStudentId())) {

            return ValidationResult.reject("Duplicate attendance");
        }

        /* -------------------------------------------------
           Session expiry
        ------------------------------------------------- */
        if (s.getEndTime() != null &&
                r.getVerifiedAt() != null &&
                r.getVerifiedAt().isAfter(s.getEndTime())) {

            return ValidationResult.reject("Session expired");
        }

        if (s.getStatus() == SessionStatus.CLOSED &&
                s.getEndTime() != null &&
                r.getVerifiedAt() != null &&
                r.getVerifiedAt().isAfter(s.getEndTime())) {

            return ValidationResult.reject("Session closed");
        }

        /* -------------------------------------------------
           Time Window Validation (OFFLINE SAFE)
        ------------------------------------------------- */

        int tokenWindow =
                s.getTokenWindowSeconds() == null ? 20 : s.getTokenWindowSeconds();

        long expectedWindow = TimeWindowUtil.windowTime(
                r.getVerifiedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant(),
                tokenWindow
        );

        if (r.getWindowTime() == null) {
            return ValidationResult.reject("Missing time-window");
        }

        // Allow +-1 window drift (offline safe)
        if (Math.abs(r.getWindowTime() - expectedWindow) > 1) {
            return ValidationResult.reject("Invalid time-window");
        }

        /* -------------------------------------------------
           Token Validation
        ------------------------------------------------- */

        String expectedToken =
                TokenGeneratorUtil.generateToken(
                        r.getSessionId(),
                        r.getWindowTime(),
                        s.getSessionSecret()
                );

        if (!expectedToken.equalsIgnoreCase(r.getToken())) {
            return ValidationResult.reject("Invalid token");
        }

        /* -------------------------------------------------
           Proof Validation
        ------------------------------------------------- */

        String safeDeviceId =
                (r.getDeviceId() == null) ? "" : r.getDeviceId();

        String expectedProof =
                HashUtil.sha256Hex(
                        r.getStudentId() + ":" +
                                r.getSessionId() + ":" +
                                r.getToken() + ":" +
                                safeDeviceId
                );

        if (!expectedProof.equalsIgnoreCase(r.getProof())) {
            return ValidationResult.reject("Invalid proof");
        }

        /* -------------------------------------------------
           Location / Distance
        ------------------------------------------------- */

        Double dist = null;

        if (s.getTeacherLat() != null &&
                s.getTeacherLng() != null &&
                r.getStudentLat() != null &&
                r.getStudentLng() != null) {

            dist = DistanceUtil.haversineMeters(
                    s.getTeacherLat(),
                    s.getTeacherLng(),
                    r.getStudentLat(),
                    r.getStudentLng()
            );

            if (s.getAllowedRadiusMeters() != null &&
                    dist > s.getAllowedRadiusMeters()) {

                return ValidationResult.reject("Outside radius");
            }
        }

        /* -------------------------------------------------
           GPS Accuracy
        ------------------------------------------------- */

        if (r.getGpsAccuracyMeters() != null &&
                s.getMaxGpsAccuracyMeters() != null) {

            if (r.getGpsAccuracyMeters() > s.getMaxGpsAccuracyMeters()) {

                return ValidationResult.reject("GPS accuracy too low");
            }
        }

        /* -------------------------------------------------
           Location Age
        ------------------------------------------------- */

        if (r.getLocationCapturedAt() != null &&
                s.getLocationMaxAgeSeconds() != null &&
                r.getVerifiedAt() != null) {

            long age = Math.abs(
                    Duration.between(
                            r.getLocationCapturedAt(),
                            r.getVerifiedAt()
                    ).getSeconds()
            );

            if (age > s.getLocationMaxAgeSeconds()) {

                return ValidationResult.reject("Location too old");
            }
        }

        return ValidationResult.accept(dist);
    }

    /* =====================================================
       Validation Result Helper
    ===================================================== */

    private static class ValidationResult {

        private final boolean accepted;
        private final String message;
        private final Double distanceMeters;

        private ValidationResult(boolean accepted,
                                 String message,
                                 Double distanceMeters) {

            this.accepted = accepted;
            this.message = message;
            this.distanceMeters = distanceMeters;
        }

        static ValidationResult accept(Double dist) {
            return new ValidationResult(true, "OK", dist);
        }

        static ValidationResult reject(String msg) {
            return new ValidationResult(false, msg, null);
        }
    }
}