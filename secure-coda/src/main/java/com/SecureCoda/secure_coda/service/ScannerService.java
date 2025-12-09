package com.SecureCoda.secure_coda.service;

import com.SecureCoda.secure_coda.client.CodaApiClient;
import com.SecureCoda.secure_coda.client.response.CodaDoc;
import com.SecureCoda.secure_coda.client.response.CodaDocResponse;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import com.SecureCoda.secure_coda.repository.AlertRepository;
import com.SecureCoda.secure_coda.service.detection.DetectionRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScannerService {

    private final CodaApiClient codaClient;
    private final AlertRepository alertRepository;

    // Spring automatically injects all classes implementing DetectionRule
    private final List<DetectionRule> detectionRules;

    @Value("${coda.api.token}")
    private String apiToken;

    public void runFullScan() {
        log.info("Starting full Security Scan...");

        try {
            // 1. Fetch all docs from Coda
            CodaDocResponse response = codaClient.listDocs(apiToken);

            if (response == null || response.getItems() == null) {
                log.warn("No documents found or API failed.");
                return;
            }

            List<CodaDoc> docs = response.getItems();
            log.info("Fetched {} documents. Analyzing...", docs.size());

            // 2. Iterate through every document
            for (CodaDoc doc : docs) {

                // 3. Run every detection rule
                for (DetectionRule rule : detectionRules) {
                    try {
                        Optional<SecurityAlert> alertOpt = rule.evaluate(doc);

                        if (alertOpt.isPresent()) {
                            SecurityAlert newAlert = alertOpt.get();


                            // Check if an OPEN alert already exists for this specific Document
                            List<SecurityAlert> existingAlerts = alertRepository.findByCodaDocIdAndStatus(
                                    doc.getId(),
                                    SecurityAlert.AlertStatus.OPEN
                            );

                            boolean isDuplicate = existingAlerts.stream().anyMatch(existing ->
                                    // Same Alert Type (e.g., both are UNUSED_DOCUMENT)
                                    existing.getAlertType().equals(newAlert.getAlertType()) &&
                                            // Same Resource ID (e.g., same Row ID for sensitive data)
                                            Objects.equals(existing.getResourceId(), newAlert.getResourceId())
                            );

                            if (isDuplicate) {
                                log.debug("Skipping duplicate alert: {} for Doc: {}", newAlert.getAlertType(), doc.getName());
                                continue;
                            }
                            // --- END DEDUPLICATION ---

                            alertRepository.save(newAlert);
                            log.info("Alert Generated: {} for Doc: {}", newAlert.getAlertType(), doc.getName());
                        }
                    } catch (Exception e) {
                        log.error("Error executing rule {} on doc {}", rule.getClass().getSimpleName(), doc.getId(), e);
                    }
                }
            }
            log.info("Scan Completed.");

        } catch (Exception e) {
            log.error("Critical error during full scan", e);
        }
    }
}
