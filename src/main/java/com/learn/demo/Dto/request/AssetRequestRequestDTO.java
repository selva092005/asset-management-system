package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetRequestRequestDTO {

    private Long assetId;      // For issue/damage tickets on existing assets
    private Long typeId;       // For procurement requests of a new asset type

    @NotBlank(message = "Requester name is required")
    private String requestedBy;

    @NotBlank(message = "Request type is required (NEW_ASSET/REPAIR/REPLACE/LOST/RETURN)")
    private String requestType;

    @NotBlank(message = "Priority is required (LOW/MEDIUM/HIGH)")
    private String priority;

    @NotBlank(message = "Description/Reason is required")
    private String description;

    private String remarks; // Used when updating status
    private String attachmentPath;
}
