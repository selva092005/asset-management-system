package com.learn.demo.model;

import java.time.LocalDate;

import org.hibernate.annotations.SQLRestriction;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@SQLRestriction("deleted = false")
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Asset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "asset_id")
    @EqualsAndHashCode.Include
    private Long assetId;

    private String assetName;
    private String serialNumber;
    private String brand;
    private String model;

    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;

    private Double cost;
    private Double depreciationRate = 20.0;

    private String status;
    private String assetCondition;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private AssetType assetType;

    @Column(unique = true)
    private String assetCode;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    // Image file name — e.g. "asset-1.jpg"
    private String imagePath;
}