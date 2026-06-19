package com.learn.demo.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "asset_request")
@SQLRestriction("deleted = false")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private AssetType assetType;

    @Column(nullable = false)
    private String requestedBy; // employee username / email

    // "NEW_ASSET", "REPAIR", "REPLACE", "LOST", "RETURN"
    @Column(nullable = false)
    private String requestType;

    // "LOW", "MEDIUM", "HIGH"
    @Column(nullable = false)
    private String priority;

    @Column(nullable = false, length = 1000)
    private String description;

    // "PENDING", "IN_PROGRESS", "APPROVED", "REJECTED", "RESOLVED"
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(length = 1000)
    private String remarks; // admin response remarks

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(length = 255)
    private String attachmentPath;
}
