package com.learn.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetRequestDTO {

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotBlank(message = "Serial number is required")
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
    private String status;

    private String assetCondition;
    private String notes;

    @NotNull(message = "Asset type ID is required")
    private Long typeId;

    @NotBlank(message = "Location name is required")
    private String locationName;
}
