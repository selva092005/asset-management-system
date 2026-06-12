package com.learn.demo.controller;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<Apiresponse> returnAsset(
            @PathVariable Long allocationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Asset returned successfully", service.returnAsset(allocationId, returnDate))
        );
    }

    // ── GET OVERVIEW STATS ────────────────────────────────────────────────────
    // Must be declared BEFORE /{id} to avoid Spring treating "overview" as an id
    @GetMapping("/overview")
    public ResponseEntity<Apiresponse> getOverview() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Overview retrieved", service.getOverview())
        );
    }

    // ── GET ALL ALLOCATIONS (paginated + optional filters) ────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getAllAllocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("assignedDate").descending());
        boolean hasFilters = (search != null) || (status != null) || (fromDate != null) || (toDate != null);

        Object result = hasFilters
            ? service.getAllAllocations(search, status, fromDate, toDate, pageable)
            : service.getAllAllocations(pageable);

        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Allocations retrieved", result)
        );
    }

    // ── GET SINGLE ALLOCATION BY ID ───────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Apiresponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Allocation retrieved", service.getById(id))
        );
    }

    // ── GET ALLOCATIONS BY ASSET ──────────────────────────────────────────────
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Apiresponse> getAllocationsByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Allocations for asset retrieved",
                service.getAllocationsByAsset(assetId))
        );
    }
}