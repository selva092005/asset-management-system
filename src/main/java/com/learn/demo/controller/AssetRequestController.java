package com.learn.demo.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.learn.demo.dto.request.AssetRequestRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class AssetRequestController {

    private final AssetRequestService requestService;

    @PostMapping
    public ResponseEntity<Apiresponse> createRequest(@Valid @RequestBody AssetRequestRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Apiresponse(HttpStatus.CREATED.value(), "Request submitted successfully",
                        requestService.createRequest(dto)));
    }

    @GetMapping
    public ResponseEntity<Apiresponse> getRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String username) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Requests retrieved",
                        requestService.getRequests(search, status, priority, requestType, username, pageable)));
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<Apiresponse> updateStatus(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) Double cost,
            @RequestParam String adminUser) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Request status updated",
                        requestService.updateStatus(requestId, status, remarks, cost, adminUser)));
    }

    @GetMapping("/overview")
    public ResponseEntity<Apiresponse> getRequestOverview(@RequestParam(required = false) String username) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Request overview stats retrieved",
                        requestService.getRequestOverview(username)));
    }
}
