package com.learn.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "asset_transfer")
@SQLRestriction("deleted = false")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false)
    private String fromLocation;

    @Column(nullable = false)
    private String toLocation;

    @Column(nullable = false)
    private String reason;

    @Column(name = "expected_date")
    private java.time.LocalDate expectedDate;

    @Column(name = "priority")
    private String priority; // "LOW" | "MEDIUM" | "HIGH"

    @Column(nullable = false)
    private String requestedBy;       // manager / admin who requested

    private String resolvedBy;        // admin who approved or rejected (null if PENDING)

    // "PENDING" | "APPROVED" | "REJECTED"
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime resolvedAt;  // null until admin acts

    @Column(name = "received_date")
    private java.time.LocalDate receivedDate;

    private String remarks;            // admin note on approve/reject
}
