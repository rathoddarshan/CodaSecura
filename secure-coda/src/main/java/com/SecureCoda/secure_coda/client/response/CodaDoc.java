package com.SecureCoda.secure_coda.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Safer: Ignores extra fields from Coda we don't need
public class CodaDoc {
    private String id;
    private String name;
    private String owner;
    private String browserLink;

    // Removed 'boolean isPublished' because Coda does not send this field.
    // It sends the 'published' object below instead.

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private DocPublished published;

    @Data
    public static class DocPublished {
        // 🚨 CRITICAL FIX: Changed "Public" to "public" (Lowercase)
        @JsonProperty("public")
        private boolean isPublic;
    }
}


