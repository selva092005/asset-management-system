package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetTransferActionDTO {

    @NotBlank(message = "Resolved-by name is required")
    private String resolvedBy;

    private String remarks;
}
