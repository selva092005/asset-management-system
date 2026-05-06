package com.learn.demo.controller;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    // ✅ CREATE SINGLE — easy to use in Swagger (single JSON object, not array)
    @PostMapping
    public ResponseEntity<Apiresponse> save(@Valid @RequestBody AssetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Asset created successfully",
                service.saveAsset(dto)
            )
        );
    }

    // ✅ CREATE BULK — separate endpoint, clearly named
    @PostMapping("/bulk")
    public ResponseEntity<Apiresponse> saveBulk(@Valid @RequestBody List<AssetRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Assets created successfully",
                service.saveAllAssets(dtos)
            )
        );
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<Apiresponse> getAllAssets() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Assets retrieved successfully", service.getAllAssets())
        );
    }

    // READ BY ID
    @GetMapping("/{assetId}")
    public ResponseEntity<Apiresponse> getAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Asset retrieved successfully", service.getAssetById(assetId))
        );
    }

    // UPDATE
    @PutMapping("/{assetId}")
    public ResponseEntity<Apiresponse> updateAsset(
            @PathVariable Long assetId,
            @Valid @RequestBody AssetRequestDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Asset updated successfully", service.updateAsset(assetId, dto))
        );
    }

    // DELETE
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Apiresponse> deleteAsset(
            @PathVariable Long assetId,
            @RequestParam String adminName) {
        service.deleteAsset(assetId, adminName);
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Deleted successfully", null)
        );
    }

    // SEARCH + PAGINATION
    @GetMapping("/search")
    public ResponseEntity<Apiresponse> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new Apiresponse(
                HttpStatus.OK.value(),
                "Assets fetched",
                service.searchAssets(name, type, location,
                        PageRequest.of(page, size, Sort.by("assetId").ascending()))
            )
        );
    }
}
