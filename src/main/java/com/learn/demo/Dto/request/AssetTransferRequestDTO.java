package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetTransferRequestDTO {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotBlank(message = "Destination location is required")
    private String toLocation;

    @NotBlank(message = "Reason is required")
    private String reason;

    private java.time.LocalDate expectedDate;

    private String priority;

    @NotBlank(message = "Requested-by name is required")
    private String requestedBy;
}
