package com.SecureCoda.secure_coda.controller;

import com.SecureCoda.secure_coda.service.ScannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScannerService scannerService;

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerScan(){
        scannerService.runFullScan();

        return ResponseEntity.ok("scan triggered Successfully.");
    }
}
