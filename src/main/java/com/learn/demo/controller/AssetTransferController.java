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

    // ── GET OVERVIEW STATS ────────────────────────────────────────────────────
    @GetMapping("/overview")
    public ResponseEntity<Apiresponse> getOverview() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfer overview", service.getOverview())
        );
    }

    // ── GET ALL (paginated + optional status filter) ──────────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getAllTransfers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Transfers retrieved",
                service.getAllTransfers(status, pageable))
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
}
