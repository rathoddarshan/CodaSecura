package com.SecureCoda.secure_coda.service;

import com.SecureCoda.secure_coda.client.CodaApiClient;
import com.SecureCoda.secure_coda.dto.AlertSummaryDto;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import com.SecureCoda.secure_coda.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final CodaApiClient codaClient;

    @Value("${coda.api.token}")
    private String apiToken;

    // --- READ METHODS (For Dashboard) ---

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

    // --- WRITE / REMEDIATION METHOD (For "Fix" Button) ---

    public void remediateAlert(Long alertId) {
        // 1. Fetch the alert from the DB
        SecurityAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));

        // 2. Check if already fixed
        if (alert.getStatus() == SecurityAlert.AlertStatus.RESOLVED) {
            log.info("Alert {} is already resolved.", alertId);
            return;
        }

        log.info("Attempting to remediate alert: {} ({}) for Doc: {}", alertId, alert.getAlertType(), alert.getCodaDocId());

        try {
            // 3. Execute Action on Coda based on Alert Type
            // We use multiple cases to handle both "UnusedDocument" and "UNUSED_DOCUMENT" naming styles
            switch (alert.getAlertType()) {
                case "UnusedDocument":
                case "UNUSED_DOCUMENT":
                case "PublicDocument":
                case "PUBLIC_DOCUMENT":
                    // Action: Delete the entire document
                    log.info("Deleting Document ID: {}", alert.getCodaDocId());
                    codaClient.deleteDoc(apiToken, alert.getCodaDocId());
                    break;

                case "SensitiveData":
                case "SENSITIVE_DATA":
                    // Action: Delete the specific row.
                    // We need to split "TableID/RowID" from the resourceId field.
                    if (alert.getResourceId() != null && alert.getResourceId().contains("/")) {
                        String[] parts = alert.getResourceId().split("/");
                        String tableId = parts[0];
                        String rowId = parts[1];

                        log.info("Deleting Row: {} from Table: {}", rowId, tableId);
                        codaClient.deleteRow(apiToken, alert.getCodaDocId(), tableId, rowId);
                    } else {
                        throw new IllegalStateException("Cannot delete row: Invalid Resource ID format. Expected 'TableID/RowID' but got: " + alert.getResourceId());
                    }
                    break;

                default:
                    throw new UnsupportedOperationException("No auto-fix available for alert type: " + alert.getAlertType());
            }

            // 4. Update Database Status to RESOLVED
            alert.setStatus(SecurityAlert.AlertStatus.RESOLVED);
            alert.setRemediationStatus(SecurityAlert.RemediationStatus.SUCCESS);
            alert.setResolvedAt(LocalDateTime.now());
            alertRepository.save(alert);
            log.info("Alert {} remediation successful.", alertId);

        } catch (Exception e) {
            log.error("Remediation failed for alert {}", alertId, e);

            // Log failure in DB
            alert.setRemediationStatus(SecurityAlert.RemediationStatus.FAILED);
            alertRepository.save(alert);

            // Re-throw so the controller knows it failed
            throw new RuntimeException("Failed to fix issue on Coda: " + e.getMessage());
        }
    }
}