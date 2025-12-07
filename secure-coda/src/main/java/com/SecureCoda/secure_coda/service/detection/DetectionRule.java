package com.SecureCoda.secure_coda.service.detection;

import com.SecureCoda.secure_coda.client.response.CodaDoc;
import com.SecureCoda.secure_coda.entity.SecurityAlert;

import java.util.Optional;

public interface DetectionRule {

    Optional<SecurityAlert> evaluate(CodaDoc doc);
}
