package com.SecureCoda.secure_coda.repository;

import com.SecureCoda.secure_coda.entity.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<SecurityAlert, Long> {
    List<SecurityAlert> findByStatus(SecurityAlert.AlertStatus status);

    List<SecurityAlert> findByCodaIdAndStatus(String codaId, SecurityAlert.AlertStatus status);

    List<SecurityAlert> FindByAlertType(String alertType);

}
