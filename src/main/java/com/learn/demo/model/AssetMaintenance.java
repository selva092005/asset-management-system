package com.learn.demo.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "asset_maintenance")
@SQLRestriction("deleted = false")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssetMaintenance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maintenance_id")
    private Long maintenanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private String vendor;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    // "REPAIRED", "WRITTEN_OFF", "ONGOING"
    @Column(nullable = false)
    private String outcome = "ONGOING";

    private String remarks;
}
