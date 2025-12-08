package com.SecureCoda.secure_coda.client;

import com.SecureCoda.secure_coda.client.response.CodaDocResponse;
import com.SecureCoda.secure_coda.client.response.CodaRowResponse;
import com.SecureCoda.secure_coda.client.response.CodaTableResponse;
import lombok.Data;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "codaClient", url = "https://coda.io/apis/v1")
public interface CodaApiClient {
    @GetMapping("/docs")
    CodaDocResponse listDocs(@RequestHeader("Authorization") String token);

    @DeleteMapping
    void deleteDoc(@RequestHeader("Authorization")String token, @PathVariable("docId") String docId);

    @GetMapping("/docs/{docId}/tables")
    CodaTableResponse listTables(@RequestHeader("Authorization") String token, @PathVariable("docId") String docId);

    @GetMapping("/doc/{docId}/tables/{tableId}/rows")
    CodaRowResponse listRows(@RequestHeader("Authorization") String token,
                             @PathVariable("docId") String docId,
                             @PathVariable("tableId") String tableId);

    @DeleteMapping("/docs/{docId}/tables/{tableId}/rows/{rowId}")
    void deleteRow(@RequestHeader("Authorization") String token,
                   @PathVariable("docId") String dcoId,
                   @PathVariable("tableId") String tableId,
                   @PathVariable("rowId") String rowId);

}

