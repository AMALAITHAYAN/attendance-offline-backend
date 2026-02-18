package com.smartattendance.service;

import com.smartattendance.dto.AttendanceSummaryDTO;
import com.smartattendance.dto.OfflineAttendanceRequest;
import com.smartattendance.entity.Attendance;
import com.smartattendance.exception.ApiException;
import com.smartattendance.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Attendance saveNew(Attendance attendance) {
        // unique constraint also exists at DB level
        if (attendanceRepository.existsBySessionIdAndStudentId(attendance.getSessionId(), attendance.getStudentId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Duplicate attendance for this session");
        }
        return attendanceRepository.save(attendance);
    }

    public List<AttendanceSummaryDTO> getSessionAttendanceSummary(String sessionId) {
        List<Attendance> list = attendanceRepository.findBySessionIdOrderByVerifiedAtAsc(sessionId);
        return list.stream().map(a -> AttendanceSummaryDTO.builder()
                .studentId(a.getStudentId())
                .confidenceScore(a.getConfidenceScore())
                .deviceId(a.getDeviceId())
                .screenResolution(a.getScreenResolution())
                .verifiedAt(a.getVerifiedAt())
                .distanceMeters(a.getDistanceMeters())
                .build()).toList();
    }

    public long countBySession(String sessionId) {
        return attendanceRepository.countBySessionId(sessionId);
    }
}
