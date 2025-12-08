package com.SecureCoda.secure_coda.client.response;

import lombok.Data;

import java.util.List;

@Data
public class CodaTableResponse {
    private List<CodaTableItem> items;


    @Data
    public static class CodaTableItem{
        private String id;
        private String name;
        private String type;
    }


}
