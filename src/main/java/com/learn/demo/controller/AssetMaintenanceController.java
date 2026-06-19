package com.learn.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learn.demo.dto.response.AssetMaintenanceDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class AssetMaintenanceController {

    private final AssetMaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<Apiresponse> logMaintenance(
            @Valid @RequestBody AssetMaintenanceDTO dto,
            @RequestParam String actionBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Apiresponse(HttpStatus.CREATED.value(), "Maintenance log created successfully", 
                        maintenanceService.logMaintenance(dto, actionBy))
        );
    }

    @GetMapping
    public ResponseEntity<Apiresponse> getAllMaintenance(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String outcome) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Maintenance logs retrieved", 
                        maintenanceService.getAllMaintenance(search, outcome))
        );
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Apiresponse> getMaintenanceByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Maintenance history for asset retrieved", 
                        maintenanceService.getMaintenanceByAsset(assetId))
        );
    }
}
