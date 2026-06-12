package com.learn.demo.dto.request;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BulkTransferRequestDTO {

    @NotEmpty(message = "Asset IDs list cannot be empty")
    private List<Long> assetIds;

    @NotBlank(message = "Destination location is required")
    private String toLocation;

    @NotBlank(message = "Reason is required")
    private String reason;

    private LocalDate expectedDate;

    private String priority;

    @NotBlank(message = "Requested-by name is required")
    private String requestedBy;
}
