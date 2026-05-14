package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoveAssetRequestDTO {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotBlank(message = "New location is required")
    private String newLocation;

    @NotBlank(message = "Moved by (username) is required")
    private String movedBy;

    private String reason; // e.g. "Reallocation", "Repair", "Loan"
}
