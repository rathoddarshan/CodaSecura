package com.SecureCoda.secure_coda.service;

import com.SecureCoda.secure_coda.client.CodaApiClient;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import com.SecureCoda.secure_coda.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class RemediationService {

    private final AlertRepository alertRepository;
    private final CodaApiClient codaClient;

    @Value("${coda.api.token}")
    private String apiToken;

    public void remediateAlert(Long alertId) {
        // 1. Fetch the alert
        SecurityAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));

        if (alert.getStatus() == SecurityAlert.AlertStatus.RESOLVED) {
            log.info("Alert {} is already resolved.", alertId);
            return;
        }

        log.info("Attempting to remediate alert: {} ({})", alertId, alert.getAlertType());

        try {
            // 2. Decide action based on Alert Type
            switch (alert.getAlertType()) {
                case "UNUSED_DOCUMENT":
                    // Action: Delete the entire document
                    codaClient.deleteDoc(apiToken, alert.getCodaDocId());
                    log.info("Successfully deleted unused document: {}", alert.getCodaDocId());
                    break;

                case "SENSITIVE_DATA":
                    // Action: Delete the specific row (Requires resourceId to be stored in alert)
                    if (alert.getResourceId() != null) {
                        // Assuming resourceId format is "tableId/rowId"
                        String[] parts = alert.getResourceId().split("/");
                        codaClient.deleteRow(apiToken, alert.getCodaDocId(), parts[0], parts[1]);
                    }
                    break;

                case "PUBLIC_DOCUMENT":
                    // Action: For now, we might just log it or delete it.
                    // (Unpublishing requires complex ACL API calls, so we default to delete for this demo)
                    log.warn("Auto-remediation for Public Docs is set to DELETE for safety.");
                    codaClient.deleteDoc(apiToken, alert.getCodaDocId());
                    break;

                default:
                    throw new UnsupportedOperationException("No remediation strategy for: " + alert.getAlertType());
            }

            // 3. Update Database Status
            alert.setStatus(SecurityAlert.AlertStatus.RESOLVED);
            alert.setRemediationStatus(SecurityAlert.RemediationStatus.SUCCESS);
            alert.setResolvedAt(LocalDateTime.now());
            alertRepository.save(alert);

        } catch (Exception e) {
            log.error("Remediation failed", e);
            alert.setRemediationStatus(SecurityAlert.RemediationStatus.FAILED);
            alertRepository.save(alert);
            throw new RuntimeException("Failed to fix issue on Coda: " + e.getMessage());
        }
    }
}