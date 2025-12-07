package com.SecureCoda.secure_coda.client.response;

import lombok.Data;

import java.util.List;

@Data
public class CodaDocResponse {
    private List<CodaDoc> items;
    private String nextPageLink;
}
