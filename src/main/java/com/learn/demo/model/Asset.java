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
import lombok.NoArgsConstructor;

@Entity
@SQLRestriction("deleted = false")   // 🔥 auto filter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset extends BaseEntity {

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

    @Column(unique = true)
    private String assetCode;   // e.g. HP-LAP-001

    @Column(length = 1000)
    private String qrCode;      // base64 QR image string

    private String locationName;
}