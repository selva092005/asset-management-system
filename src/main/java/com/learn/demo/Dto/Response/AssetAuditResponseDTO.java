package com.learn.demo.dto.response;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AssetAuditResponseDTO {
    private Long auditId;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private String typeName;
    private String auditedBy;
    private LocalDate auditDate;
    private String status;
    private String remarks;
    private String actionTaken;
    private Boolean screenOk;
    private Boolean keyboardOk;
    private Boolean chargerOk;
    private Boolean batteryOk;
}
