package com.SecureCoda.secure_coda.service.detection;

import com.SecureCoda.secure_coda.client.response.CodaDoc;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class UnusedDocRule implements DetectionRule{

    @Value("${coda.scan.unused-days:10}")
    private int unusedDaysThreshold;

    @Override
    public Optional<SecurityAlert> evaluate(CodaDoc doc){
        long timeSinceUpdate = ChronoUnit.MINUTES.between(doc.getUpdatedAt(), LocalDateTime.now());
        if(timeSinceUpdate > 5){
            SecurityAlert alert = SecurityAlert.builder()
                    .alertType("UnusedDocument")
                    .severity(SecurityAlert.AlertSeverity.LOW)
                    .status(SecurityAlert.AlertStatus.OPEN)
                    .codaDocId(doc.getId())
                    .docName(doc.getName())
                    .description("Document has not been modified for " + timeSinceUpdate + " days.")
                    .detectedAt(LocalDateTime.now())
                    .remediationStatus(SecurityAlert.RemediationStatus.PENDING)
                    .build();

            return Optional.of(alert);
        }

        return Optional.empty();
    }


}