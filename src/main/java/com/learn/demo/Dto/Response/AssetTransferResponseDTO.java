package com.learn.demo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssetTransferResponseDTO {

    private Long transferId;

    private Long assetId;
    private String assetName;
    private String assetCode;

    private String fromLocation;
    private String toLocation;

    private String reason;
    private String requestedBy;
    private String resolvedBy;
    private String status;          // "PENDING" | "APPROVED" | "REJECTED"
    private String remarks;

    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}
