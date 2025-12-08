package com.SecureCoda.secure_coda.service.detection;

import com.SecureCoda.secure_coda.client.response.CodaDoc;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PublicShareRule implements DetectionRule{
    @Override
    public Optional<SecurityAlert> evaluate(CodaDoc doc){
        boolean isPublic = doc.getPublished() != null && doc.getPublished().isPublic();

        if(isPublic){
            return Optional.of(SecurityAlert.builder()
                            .alertType("Public_Doc")
                            .severity(SecurityAlert.AlertSeverity.HIGH)
                            .status(SecurityAlert.AlertStatus.OPEN)
                            .codaDocId(doc.getId())
                            .docName(doc.getName())
                            .description("Document is published and publicly available " + doc.getBrowserLink())
                            .detectAt(LocalDateTime.now())
                            .remediationStatus(SecurityAlert.RemediationStatus.PENDING)
                            .build());
        }

        return Optional.empty();
    }
}
