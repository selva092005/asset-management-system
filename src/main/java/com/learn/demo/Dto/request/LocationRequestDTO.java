package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

@Data
public class LocationRequestDTO {

    @NotBlank(message = "Location name is required")
    private String locationName;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private java.math.BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private java.math.BigDecimal longitude;

    private String locationType;
    private String address;
    private String contactPerson;
}
