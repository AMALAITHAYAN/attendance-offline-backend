package com.smartattendance.repository;

import com.smartattendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsBySessionIdAndStudentId(String sessionId, String studentId);
    List<Attendance> findBySessionIdOrderByVerifiedAtAsc(String sessionId);
    long countBySessionId(String sessionId);
}
