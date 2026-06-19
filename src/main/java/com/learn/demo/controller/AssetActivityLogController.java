package com.learn.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetActivityLogService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class AssetActivityLogController {

    private final AssetActivityLogService activityLogService;

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Apiresponse> getLogsByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
                new Apiresponse(200, "Activity logs retrieved successfully", 
                        activityLogService.getLogsByAsset(assetId))
        );
    }
}
