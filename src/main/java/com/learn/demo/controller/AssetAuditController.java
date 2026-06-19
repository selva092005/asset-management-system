package com.learn.demo.controller;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learn.demo.dto.request.AssetAuditRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetAuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AssetAuditController {

    private final AssetAuditService auditService;

    @PostMapping
    public ResponseEntity<Apiresponse> createAudit(@Valid @RequestBody AssetAuditRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Apiresponse(HttpStatus.CREATED.value(), "Asset audited successfully", auditService.createAudit(dto))
        );
    }

    @GetMapping
    public ResponseEntity<Apiresponse> getAudits(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Audits retrieved", auditService.getAudits(search, status, fromDate, toDate))
        );
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Apiresponse> getAuditsByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Audits for asset retrieved", auditService.getAuditsByAsset(assetId))
        );
    }

    @GetMapping("/overview")
    public ResponseEntity<Apiresponse> getAuditOverview() {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Audit overview retrieved", auditService.getAuditOverview())
        );
    }
}
