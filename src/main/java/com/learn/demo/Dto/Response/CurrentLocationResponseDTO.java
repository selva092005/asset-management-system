package com.learn.demo.dto.response;

import lombok.Data;

@Data
public class CurrentLocationResponseDTO {

    private String city;       // e.g. "Chennai"
    private String region;     // e.g. "Tamil Nadu"
    private String country;    // e.g. "India"
    private String countryCode;// e.g. "IN"
    private String timezone;   // e.g. "Asia/Kolkata"
    private String ip;         // e.g. "103.x.x.x"
    private Double latitude;
    private Double longitude;
}