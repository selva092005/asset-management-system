package com.learn.demo.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.request.AssetTransferActionDTO;
import com.learn.demo.dto.request.AssetTransferRequestDTO;
import com.learn.demo.dto.request.BulkTransferActionDTO;
import com.learn.demo.dto.request.BulkTransferRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetTransferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class AssetTransferController {

    private final AssetTransferService service;

    // ── REQUEST TRANSFER (Manager + Admin) ────────────────────────────────────
    @PostMapping
    public ResponseEntity<Apiresponse> requestTransfer(@Valid @RequestBody AssetTransferRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Transfer request created", service.requestTransfer(dto))
        );
    }

    // ── REQUEST BULK TRANSFER (Manager + Admin) ───────────────────────────────
    @PostMapping("/bulk")
    public ResponseEntity<Apiresponse> requestBulkTransfer(@Valid @RequestBody BulkTransferRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Bulk transfer requests created", service.requestBulkTransfer(dto))
        );
    }

    // ── APPROVE (Admin only) ──────────────────────────────────────────────────
    @PutMapping("/{id}/approve")
    public ResponseEntity<Apiresponse> approve(
            @PathVariable Long id,
            @Valid @RequestBody AssetTransferActionDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfer approved", service.approveTransfer(id, dto))
        );
    }

    // ── REJECT (Admin only) ───────────────────────────────────────────────────
    @PutMapping("/{id}/reject")
    public ResponseEntity<Apiresponse> reject(
            @PathVariable Long id,
            @Valid @RequestBody AssetTransferActionDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfer rejected", service.rejectTransfer(id, dto))
        );
    }

    // ── RECEIVE / CONFIRM RECEIPT (Manager + Admin) ───────────────────────────
    @PutMapping("/{id}/receive")
    public ResponseEntity<Apiresponse> receive(
            @PathVariable Long id,
            @Valid @RequestBody AssetTransferActionDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfer completed / receipt confirmed", service.receiveTransfer(id, dto))
        );
    }

    // ── APPROVE BULK TRANSFERS (Admin only) ──────────────────────────────────
    @PutMapping("/bulk/approve")
    public ResponseEntity<Apiresponse> approveBulk(
            @Valid @RequestBody BulkTransferActionDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Bulk transfers approved", service.approveBulkTransfers(dto))
        );
    }

    // ── REJECT BULK TRANSFERS (Admin only) ───────────────────────────────────
    @PutMapping("/bulk/reject")
    public ResponseEntity<Apiresponse> rejectBulk(
            @Valid @RequestBody BulkTransferActionDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Bulk transfers rejected", service.rejectBulkTransfers(dto))
        );
    }

    // ── RECEIVE BULK TRANSFERS (Manager + Admin) ─────────────────────────────
    @PutMapping("/bulk/receive")
    public ResponseEntity<Apiresponse> receiveBulk(
            @Valid @RequestBody BulkTransferActionDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Bulk transfers completed / receipt confirmed", service.receiveBulkTransfers(dto))
        );
    }

    // ── GET OVERVIEW STATS ────────────────────────────────────────────────────
    @GetMapping("/overview")
    public ResponseEntity<Apiresponse> getOverview() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfer overview", service.getOverview())
        );
    }

    // ── GET ALL (paginated + optional status filter + optional date filters) ──────────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getAllTransfers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfers retrieved",
                service.getAllTransfers(status, startDate, endDate, pageable))
        );
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Apiresponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfer retrieved", service.getById(id))
        );
    }

    // ── GET BY ASSET ──────────────────────────────────────────────────────────
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Apiresponse> getByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfers for asset", service.getTransfersByAsset(assetId))
        );
    }

    // ── EXPORT TO EXCEL ───────────────────────────────────────────────────────
    @GetMapping("/export")
    public ResponseEntity<org.springframework.core.io.Resource> exportToExcel() {
        java.io.ByteArrayOutputStream out = service.exportToExcel();
        org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asset_transfers_log.xlsx")
            .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(out.size())
            .body(resource);
    }
}
