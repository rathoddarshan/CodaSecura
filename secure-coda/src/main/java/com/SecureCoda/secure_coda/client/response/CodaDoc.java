package com.SecureCoda.secure_coda.client.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodaDoc {
    private String id;
    private String name;
    private String owner;
    private String browserLink;
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
