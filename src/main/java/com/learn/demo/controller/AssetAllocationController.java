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

import com.learn.demo.dto.request.AssetAllocationRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetAllocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AssetAllocationController {

    private final AssetAllocationService service;

    // ── ALLOCATE ASSET ────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Apiresponse> allocate(@Valid @RequestBody AssetAllocationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Asset allocated successfully", service.allocate(dto))
        );
    }

    // ── RETURN ASSET ──────────────────────────────────────────────────────────
    @PutMapping("/{allocationId}/return")
    public ResponseEntity<Apiresponse> returnAsset(@PathVariable Long allocationId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Asset returned successfully", service.returnAsset(allocationId))
        );
    }

    // ── GET ALL ALLOCATIONS (paginated) ───────────────────────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getAllAllocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Allocations retrieved",
                service.getAllAllocations(PageRequest.of(page, size, Sort.by("assignedDate").descending())))
        );
    }

    // ── GET ALLOCATIONS BY ASSET ──────────────────────────────────────────────
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Apiresponse> getAllocationsByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Allocations for asset retrieved", service.getAllocationsByAsset(assetId))
        );
    }
}
