package com.SecureCoda.secure_coda.dto;

import com.SecureCoda.secure_coda.entity.SecurityAlert;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertSummaryDto {

    private Long id;
    private String alertType;
    private String severity;
    private String docName;
    private String description;
    private String status;
    private LocalDateTime detectedAt;

    public static AlertSummaryDto fromEntity(SecurityAlert alert){
        return AlertSummaryDto.builder()
                .id(alert.getId())
                .alertType(alert.getAlertType())
                .severity(alert.getSeverity().name())
                .docName(alert.getDocName())
                .description(alert.getDescription())
                .status(alert.getStatus().name())
                .detectedAt(alert.getDetectedAt())
                .build();
    }

}
