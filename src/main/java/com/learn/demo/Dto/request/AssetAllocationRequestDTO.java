package com.learn.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetAllocationRequestDTO {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotBlank(message = "Assigned-to name is required")
    private String assignedTo;

    @NotBlank(message = "Assigned-by name is required")
    private String assignedBy;

    @NotNull(message = "Assigned date is required")
    private LocalDate assignedDate;

    private LocalDate expectedReturnDate;

    private String remarks;
}
