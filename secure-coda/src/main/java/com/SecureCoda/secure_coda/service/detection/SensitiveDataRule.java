package com.SecureCoda.secure_coda.service.detection;

import com.SecureCoda.secure_coda.client.CodaApiClient;
import com.SecureCoda.secure_coda.client.response.CodaDoc;
import com.SecureCoda.secure_coda.client.response.CodaRowResponse;
import com.SecureCoda.secure_coda.client.response.CodaTableResponse;
import com.SecureCoda.secure_coda.entity.SecurityAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveDataRule implements DetectionRule{

    private final CodaApiClient codaClient;

    @Value("${coda.api.token}")
    private String apiToken;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private static final String SENSITIVE_KEYWORD = "password";

    @Override
    public Optional<SecurityAlert> evaluate(CodaDoc doc) {
        try {
            CodaTableResponse tables = codaClient.listTables(apiToken, doc.getId());
            if (tables.getItems() == null) return Optional.empty();

            for (CodaTableResponse.CodaTableItem table : tables.getItems()) {


                try {
                    // Only scan actual tables (optional check, depends on API response)
                    // if (!"table".equals(table.getType())) continue;

                    CodaRowResponse rows = codaClient.listRows(apiToken, doc.getId(), table.getId());
                    if (rows.getItems() == null) continue;

                    for (CodaRowResponse.CodaRowItem row : rows.getItems()) {
                        // ... existing scanning logic ...
                        for (Object cellValue : row.getValues().values()) {
                            String text = String.valueOf(cellValue);
                            if (isSensitive(text)) {
                                return Optional.of(SecurityAlert.builder()
                                        .alertType("SENSITIVE_DATA")
                                        .severity(SecurityAlert.AlertSeverity.MEDIUM)
                                        .status(SecurityAlert.AlertStatus.OPEN)
                                        .codaDocId(doc.getId())
                                        .docName(doc.getName())
                                        .resourceId(table.getId() + "/" + row.getId())
                                        .description("Sensitive data in Table '" + table.getName() + "': " + text)
                                        .detectedAt(LocalDateTime.now()) // Ensure this is here
                                        .remediationStatus(SecurityAlert.RemediationStatus.PENDING)
                                        .build());
                            }
                        }
                    }
                } catch (Exception e) {
                    // Log the error but CONTINUE to the next table
                    log.warn("Could not read rows for table '{}' in doc '{}'. It might be a View or Restricted. Error: {}",
                            table.getName(), doc.getName(), e.getMessage());
                }
                // --- END TRY-CATCH ---
            }
        } catch (Exception e) {
            log.error("Error scanning doc {} for sensitive data", doc.getId(), e);
        }
        return Optional.empty();
    }
    private boolean isSensitive(String text) {
        if (text == null) return false;
        // Check for keyword "password" (case-insensitive)
        if (text.toLowerCase().contains(SENSITIVE_KEYWORD)) return true;
        // Check for Email pattern
        return EMAIL_PATTERN.matcher(text).find();
    }


}

