package com.learn.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetDisposalRequestDTO {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Disposal date is required")
    private LocalDate disposalDate;

    @NotBlank(message = "Disposal method is required")
    @jakarta.validation.constraints.Pattern(
        regexp = "^(SOLD|SCRAPPED|DONATED|DAMAGED)$",
        message = "Disposal method must be SOLD, SCRAPPED, DONATED or DAMAGED"
    )
    private String disposalMethod;   // SOLD | SCRAPPED | DONATED | DAMAGED

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Disposed-by name is required")
    private String disposedBy;

    private Double disposalValue;
}
