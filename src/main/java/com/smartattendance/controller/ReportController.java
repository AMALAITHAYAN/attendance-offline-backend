package com.smartattendance.controller;

import com.smartattendance.dto.SessionSummaryDTO;
import com.smartattendance.entity.Attendance;
import com.smartattendance.entity.Session;
import com.smartattendance.repository.AttendanceRepository;
import com.smartattendance.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final SessionService sessionService;
    private final AttendanceRepository attendanceRepository;

    public ReportController(SessionService sessionService, AttendanceRepository attendanceRepository) {
        this.sessionService = sessionService;
        this.attendanceRepository = attendanceRepository;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/session/{sessionId}/summary")
    public ResponseEntity<SessionSummaryDTO> sessionSummary(@PathVariable String sessionId) {
        Session s = sessionService.getSessionEntity(sessionId);
        List<Attendance> list = attendanceRepository.findBySessionIdOrderByVerifiedAtAsc(sessionId);

        double avg = list.stream()
                .filter(a -> a.getConfidenceScore() != null)
                .mapToInt(Attendance::getConfidenceScore)
                .average()
                .orElse(0.0);

        SessionSummaryDTO dto = SessionSummaryDTO.builder()
                .sessionId(s.getSessionId())
                .status(s.getStatus())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .totalAttendance(list.size())
                .avgConfidenceScore(list.isEmpty() ? null : avg)
                .build();

        return ResponseEntity.ok(dto);
    }
}
