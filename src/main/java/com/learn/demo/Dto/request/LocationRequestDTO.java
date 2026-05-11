package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationRequestDTO {

    @NotBlank(message = "Location name is required")
    private String locationName;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}
