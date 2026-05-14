package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveCurrentLocationRequestDTO {

    /**
     * The location name detected from IP or manually confirmed by user.
     * Example: "Chennai"
     */
    @NotBlank(message = "Location name is required")
    private String locationName;

    /**
     * The company this location belongs to.
     */
    @NotNull(message = "Company ID is required")
    private Long companyId;
}