package com.smartattendance.controller;

import com.smartattendance.dto.OfflineAttendanceRequest;
import com.smartattendance.dto.OfflineSyncResponse;
import com.smartattendance.service.ProofValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OfflineSyncController {

    private final ProofValidationService proofValidationService;

    public OfflineSyncController(ProofValidationService proofValidationService) {
        this.proofValidationService = proofValidationService;
    }

    /**
     * Students upload their offline proofs later when internet is available.
     */
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @PostMapping("/offline-sync")
    public ResponseEntity<OfflineSyncResponse> sync(@Valid @RequestBody List<OfflineAttendanceRequest> records) {
        return ResponseEntity.ok(proofValidationService.validateAndStore(records));
    }
}
