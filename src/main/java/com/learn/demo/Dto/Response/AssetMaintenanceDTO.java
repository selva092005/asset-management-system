package com.learn.demo.dto.response;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetMaintenanceDTO {
    private Long maintenanceId;

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    private String assetName;
    private String assetCode;

    @NotBlank(message = "Vendor is required")
    private String vendor;

    @NotNull(message = "Cost is required")
    private Double cost;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private String outcome; // "REPAIRED", "WRITTEN_OFF", "ONGOING"
    private String remarks;
}
