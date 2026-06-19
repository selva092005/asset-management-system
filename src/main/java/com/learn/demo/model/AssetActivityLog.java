package com.learn.demo.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asset_activity_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // "CREATED", "ALLOCATED", "RETURNED", "TRANSFERRED", "AUDITED", "REPAIRED", "DISPOSED"
    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String actionBy;

    @Column(nullable = false)
    private LocalDateTime actionDate;

    @Column(length = 1000)
    private String details;
}
