package com.learn.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssetRequestDTO {

    @NotBlank(message = "Asset name is required")
    private String assetName;

    private String serialNumber;

    private String brand;
    private String model;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    private LocalDate warrantyExpiry;

    @NotNull(message = "Cost is required")
    @Min(value = 0, message = "Cost must be zero or positive")
    private Double cost;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "AVAILABLE|ASSIGNED|DAMAGED|UNDER_MAINTENANCE|IN_TRANSIT", message = "Status must be AVAILABLE, ASSIGNED, DAMAGED, UNDER_MAINTENANCE or IN_TRANSIT")
    private String status;

    @Pattern(regexp = "^(GOOD|FAIR|POOR)$", message = "Condition must be GOOD, FAIR or POOR")
    private String assetCondition;
    private String notes;

    @NotNull(message = "Asset type ID is required")
    private Long typeId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    private String imagePath;   // ✅ FIX: file name returned by /api/files/upload, saved to DB
}