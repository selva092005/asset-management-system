package com.learn.demo.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "asset_audit")
@SQLRestriction("deleted = false")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssetAudit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private String auditedBy;

    @Column(nullable = false)
    private LocalDate auditDate;

    // "GOOD", "DAMAGED", "LOST"
    @Column(nullable = false)
    private String status;

    private String remarks;

    // "REPAIRED", "REPLACED", "DISPOSED", "NONE"
    private String actionTaken = "NONE";

    // Inspection Checklist
    private Boolean screenOk = true;
    private Boolean keyboardOk = true;
    private Boolean chargerOk = true;
    private Boolean batteryOk = true;
}
