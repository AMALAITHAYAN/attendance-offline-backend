package com.smartattendance.controller;

import com.smartattendance.dto.CloseSessionResponse;
import com.smartattendance.dto.SessionResponse;
import com.smartattendance.dto.StartSessionRequest;
import com.smartattendance.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Teacher starts a session (stores config + generates secret for token generation).
     */
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/start")
    public ResponseEntity<SessionResponse> start(@Valid @RequestBody StartSessionRequest req) {
        return ResponseEntity.ok(sessionService.startSession(req));
    }

    /**
     * Teacher closes a session early.
     */
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/{id}/close")
    public ResponseEntity<CloseSessionResponse> close(@PathVariable("id") String sessionId) {
        return ResponseEntity.ok(sessionService.closeSession(sessionId));
    }

    /**
     * Get session config (secret hidden by default).
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> get(@PathVariable("id") String sessionId,
                                               @RequestParam(value = "includeSecret", defaultValue = "false") boolean includeSecret) {
        // Always hide secret on this public endpoint
        return ResponseEntity.ok(sessionService.getSession(sessionId, false));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/teacher-view")
    public ResponseEntity<SessionResponse> getTeacherView(@PathVariable("id") String sessionId,
                                                          @RequestParam(value = "includeSecret", defaultValue = "true") boolean includeSecret) {
        return ResponseEntity.ok(sessionService.getSession(sessionId, includeSecret));
    }
}
