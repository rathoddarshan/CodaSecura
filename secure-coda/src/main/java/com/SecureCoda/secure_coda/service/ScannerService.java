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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScannerService {

    private final CodaApiClient codaClient;
    private final AlertRepository alertRepository;

    private final List<DetectionRule> detectionRules;

    @Value("${coda.api.token}")
    private String apiToken;

    public void runFullScan(){
        log.info("Starting full Security Scan...");

        CodaDocResponse response = codaClient.listDocs(apiToken);

        if(response == null || response.getItems() == null){
            log.warn("no Docs found or API failed");
            return;
        }

        List<CodaDoc> docs = response.getItems();
        log.info("Fetched {} documents. Analyzing... ", docs.size());

        for (CodaDoc doc : docs) {

            // 3. Run every detection rule against the document
            for (DetectionRule rule : detectionRules) {
                Optional<SecurityAlert> alertOpt = rule.evaluate(doc);

                if (alertOpt.isPresent()) {
                    SecurityAlert alert = alertOpt.get();

                    // 4. Save to DB (Check duplicates logic can be added here later)
                    alertRepository.save(alert);
                    log.info("Alert Generated: {} for Doc: {}", alert.getAlertType(), doc.getName());
                }
            }
        }
        log.info("Scan Completed. ");
    }
}
