package com.learn.demo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.Dto.Response.Apiresponse;
import com.learn.demo.model.Asset;
import com.learn.demo.service.AssetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    // CREATE
    @PostMapping
    public ResponseEntity<Apiresponse> save(@RequestBody List<Asset> assets) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Assets created successfully",
                service.saveAllAssets(assets)
            )
        );
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<Apiresponse> getAllAssets() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(),
            "Assets retrieved successfully",
            service.getAllAssets())
        );
    }

    // READ BY ID
    @GetMapping("/{assetId}")
    public ResponseEntity<Apiresponse> getAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(),
            "Asset retrieved successfully",
            service.getAssetById(assetId))
        );
    }

    // UPDATE
    @PutMapping("/{assetId}")
    public ResponseEntity<Apiresponse> updateAsset(
            @PathVariable Long assetId,
            @RequestBody Asset asset) {

        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(),
            "Asset updated successfully",
            service.updateAsset(assetId, asset))
        );
    }

    // DELETE
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Apiresponse> deleteAsset(@PathVariable Long assetId) {
        service.deleteAsset(assetId);
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(),
            "Deleted successfully", null)
        );
    }

    // SEARCH + PAGINATION 🔥
    @GetMapping("/search")
    public ResponseEntity<Apiresponse> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Asset> result = service.searchAssets(
                name, type, location, PageRequest.of(page, size)
        );

        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(),
            "Assets fetched", result)
        );
    }
}