package com.learn.demo.dto.response;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AssetRequestResponseDTO {
    private Long requestId;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private Long typeId;
    private String typeName;
    private String requestedBy;
    private String requestType;
    private String priority;
    private String description;
    private String status;
    private String remarks;
    private LocalDate requestDate;
    private String attachmentPath;
}
