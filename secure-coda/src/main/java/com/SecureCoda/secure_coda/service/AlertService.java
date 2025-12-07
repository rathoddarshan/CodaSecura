package com.SecureCoda.secure_coda.service;

import com.SecureCoda.secure_coda.dto.AlertSummaryDto;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import com.SecureCoda.secure_coda.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;


    public List<AlertSummaryDto> getOpenAlerts() {
        return alertRepository.findByStatus(SecurityAlert.AlertStatus.OPEN)
                .stream()
                .map(AlertSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AlertSummaryDto> getAllAlertsHistory() {
        return alertRepository.findAll()
                .stream()
                .map(AlertSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }
}
