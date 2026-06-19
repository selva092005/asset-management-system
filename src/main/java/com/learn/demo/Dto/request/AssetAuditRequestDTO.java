package com.learn.demo.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetAuditRequestDTO {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotBlank(message = "Audited-by name is required")
    private String auditedBy;

    @NotNull(message = "Audit date is required")
    private LocalDate auditDate;

    @NotBlank(message = "Status is required (GOOD/DAMAGED/LOST)")
    private String status;

    private String remarks;

    private String actionTaken;

    private Boolean screenOk = true;
    private Boolean keyboardOk = true;
    private Boolean chargerOk = true;
    private Boolean batteryOk = true;
}
