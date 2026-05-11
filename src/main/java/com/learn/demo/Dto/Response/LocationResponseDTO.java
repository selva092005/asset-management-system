package com.learn.demo.dto.response;

import lombok.Data;

@Data
public class LocationResponseDTO {
    private Long locationId;
    private String locationName;
    private String locationCode; // e.g. "CH" from "Chennai"
    private Long companyId;
    private String companyName;
}
