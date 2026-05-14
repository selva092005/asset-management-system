package com.learn.demo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AssetResponseDTO {
    private Long assetId;
    private String assetName;
    private String serialNumber;
    private String brand;
    private String model;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    private Double cost;
    private String status;
    private String assetCondition;
    private String notes;
    private Long typeId;       // ✅ ADDED — needed by frontend edit form to pre-select dropdown
    private String typeName;
    private String assetCode;
    private String qrCode;
    private String locationName;
    private String imagePath;   // file name stored in uploads/ folder, e.g. "abc123.jpg"
}