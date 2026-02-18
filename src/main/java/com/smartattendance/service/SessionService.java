package com.smartattendance.service;

import com.smartattendance.dto.CloseSessionResponse;
import com.smartattendance.dto.SessionResponse;
import com.smartattendance.dto.StartSessionRequest;
import com.smartattendance.entity.Session;
import com.smartattendance.entity.SessionStatus;
import com.smartattendance.exception.ApiException;
import com.smartattendance.repository.SessionRepository;
import com.smartattendance.util.RandomUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public SessionResponse startSession(StartSessionRequest req) {
        LocalDateTime now = LocalDateTime.now();
        String sessionId = RandomUtil.newSessionId();
        String secret = RandomUtil.newSecret();

        double maxAcc = req.getMaxGpsAccuracyMeters() == null ? 50.0 : req.getMaxGpsAccuracyMeters();
        int maxAge = req.getLocationMaxAgeSeconds() == null ? 30 : req.getLocationMaxAgeSeconds();

        Session s = Session.builder()
                .sessionId(sessionId)
                .sessionSecret(secret)
                .qrRefreshIntervalSeconds(req.getQrRefreshIntervalSeconds())
                .tokenWindowSeconds(req.getTokenWindowSeconds())
                .allowedRadiusMeters(req.getAllowedRadiusMeters())
                .durationMinutes(req.getDurationMinutes())
                .teacherLat(req.getTeacherLat())
                .teacherLng(req.getTeacherLng())
                .maxGpsAccuracyMeters(maxAcc)
                .locationMaxAgeSeconds(maxAge)
                .startTime(now)
                .endTime(now.plusMinutes(req.getDurationMinutes()))
                .status(SessionStatus.ACTIVE)
                .build();

        s = sessionRepository.save(s);
        return toResponse(s, true);
    }

    public CloseSessionResponse closeSession(String sessionId) {
        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found"));

        s.setStatus(SessionStatus.CLOSED);
        s.setEndTime(LocalDateTime.now());
        sessionRepository.save(s);

        return CloseSessionResponse.builder()
                .sessionId(s.getSessionId())
                .status(s.getStatus())
                .endTime(s.getEndTime())
                .build();
    }

    public Session getSessionEntity(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    public SessionResponse getSession(String sessionId, boolean includeSecret) {
        Session s = getSessionEntity(sessionId);
        return toResponse(s, includeSecret);
    }

    private SessionResponse toResponse(Session s, boolean includeSecret) {
        return SessionResponse.builder()
                .sessionId(s.getSessionId())
                .sessionSecret(includeSecret ? s.getSessionSecret() : null)
                .qrRefreshIntervalSeconds(s.getQrRefreshIntervalSeconds())
                .tokenWindowSeconds(s.getTokenWindowSeconds())
                .allowedRadiusMeters(s.getAllowedRadiusMeters())
                .durationMinutes(s.getDurationMinutes())
                .teacherLat(s.getTeacherLat())
                .teacherLng(s.getTeacherLng())
                .maxGpsAccuracyMeters(s.getMaxGpsAccuracyMeters())
                .locationMaxAgeSeconds(s.getLocationMaxAgeSeconds())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus())
                .build();
    }
}
