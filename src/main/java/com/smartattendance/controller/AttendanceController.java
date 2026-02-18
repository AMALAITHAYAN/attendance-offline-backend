package com.smartattendance.controller;

import com.smartattendance.dto.AttendanceSummaryDTO;
import com.smartattendance.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceSummaryDTO>> listBySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(attendanceService.getSessionAttendanceSummary(sessionId));
    }
}
