package com.learn.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.request.AssetDisposalRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetDisposalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/disposals")
@RequiredArgsConstructor
public class AssetDisposalController {

    private final AssetDisposalService service;

    // ── DISPOSE ASSET ─────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Apiresponse> dispose(@Valid @RequestBody AssetDisposalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Asset disposed successfully", service.dispose(dto))
        );
    }

    // ── GET ALL DISPOSAL RECORDS ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getAllDisposals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String method) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Disposal records retrieved", service.getAllDisposals(search, method))
        );
    }

    // ── GET SINGLE DISPOSAL RECORD ────────────────────────────────────────────
    @GetMapping("/{disposalId}")
    public ResponseEntity<Apiresponse> getDisposalById(@PathVariable Long disposalId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Disposal record retrieved", service.getDisposalById(disposalId))
        );
    }
}
