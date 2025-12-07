package com.SecureCoda.secure_coda.controller;

import com.SecureCoda.secure_coda.dto.AlertSummaryDto;
import com.SecureCoda.secure_coda.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public List<AlertSummaryDto> getOpenAlerts(){
        return alertService.getOpenAlerts();
    }

    @GetMapping("/history")
    public List<AlertSummaryDto> getAllAlerts() {
        return alertService.getAllAlertsHistory();
    }
}
