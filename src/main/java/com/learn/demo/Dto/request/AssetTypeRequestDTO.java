package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetTypeRequestDTO {

    @NotBlank(message = "Type name is required")
    private String typeName;
}
