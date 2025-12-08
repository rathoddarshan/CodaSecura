package com.SecureCoda.secure_coda.client.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CodaRowResponse {
    private List<CodaRowItem> items;

    @Data
    public static class CodaRowItem{
        private String id;
        private String name;
        private Map<String, Object> values;
    }
}
