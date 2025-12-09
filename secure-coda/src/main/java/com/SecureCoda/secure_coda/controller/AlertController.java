package com.SecureCoda.secure_coda.controller;

import com.SecureCoda.secure_coda.dto.AlertSummaryDto;
import com.SecureCoda.secure_coda.service.AlertService;
import com.SecureCoda.secure_coda.service.RemediationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final RemediationService remediationService;

    @GetMapping
    public List<AlertSummaryDto> getOpenAlerts(){
        return alertService.getOpenAlerts();
    }

    @GetMapping("/history")
    public List<AlertSummaryDto> getAllAlerts() {
        return alertService.getAllAlertsHistory();
    }

    @PostMapping("/{id}/remediate")
    public ResponseEntity<String> remediateAlert(@PathVariable Long id){
        alertService.remediateAlert(id);

        return ResponseEntity.ok("Remediation action executed Successfully.");
    }
}
