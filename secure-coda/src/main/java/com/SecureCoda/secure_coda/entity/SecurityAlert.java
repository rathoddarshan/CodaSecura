package com.SecureCoda.secure_coda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alertType;

    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    @Column(length = 100)
    private String description;

    private String codaId;

    private String docName;

    private String resourceId;

    private LocalDateTime detectAt;

    private LocalDateTime resolvedAt;

    @Enumerated(EnumType.STRING)
    private RemediationStatus remediationStatus;

    public enum AlertSeverity {HIGH, MEDIUM, LOW}
    public enum AlertStatus {OPEN, RESOLVED, IGNORED}
    public enum RemediationStatus {PENDING, SUCCESS, FAILED, MANUAL_REQUIRED}

}
