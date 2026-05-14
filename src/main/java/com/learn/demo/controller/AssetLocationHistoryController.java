package com.learn.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.request.MoveAssetRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetLocationHistoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asset-history")
@RequiredArgsConstructor
public class AssetLocationHistoryController {

    private final AssetLocationHistoryService service;

    /**
     * POST /api/asset-history/move
     *
     * Move an asset to a new location.
     * This saves one history row AND updates the asset's current locationName
     * inside a single database transaction.
     *
     * Request body example:
     * {
     *   "assetId": 1,
     *   "newLocation": "Office Room 2",
     *   "movedBy": "Ravi Kumar",
     *   "reason": "Reallocation"
     * }
     */
    @PostMapping("/move")
    public ResponseEntity<Apiresponse> moveAsset(@Valid @RequestBody MoveAssetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Asset moved successfully",
                service.moveAsset(dto)
            )
        );
    }

    /**
     * GET /api/asset-history/{assetId}
     *
     * Returns the full location history for one asset, newest move first.
     *
     * Example response item:
     * {
     *   "historyId": 3,
     *   "assetId": 1,
     *   "assetName": "Laptop #1087",
     *   "assetCode": "H-CH-IT-00001",
     *   "fromLocation": "Office Room 1",
     *   "toLocation": "Office Room 2",
     *   "movedBy": "Ravi Kumar",
     *   "movedAt": "2025-01-12T10:42:00",
     *   "reason": "Reallocation"
     * }
     */
    @GetMapping("/{assetId}")
    public ResponseEntity<Apiresponse> getHistory(@PathVariable Long assetId) {
        List<?> history = service.getHistoryByAssetId(assetId);
        return ResponseEntity.ok(
            new Apiresponse(
                HttpStatus.OK.value(),
                "Location history retrieved successfully",
                history
            )
        );
    }
}
