package com.learn.demo.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data   //  This replaces getters + setters
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
@Column(name = "asset_id")
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
    @ManyToOne
    @JoinColumn(name = "type_id")
    private AssetType assetType;

    // private String assetTypeName;
    private String locationName;
}