package com.learn.demo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssetLocationHistoryResponseDTO {

    private Long historyId;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private String fromLocation;
    private String toLocation;
    private String movedBy;
    private LocalDateTime movedAt;
    private String reason;
}
