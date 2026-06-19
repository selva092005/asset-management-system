package com.learn.demo.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDTO {

    // ── Asset Summary ─────────────────────────────────────────────────────────
    private long totalAssets;
    private long available;
    private long assigned;
    private long disposed;
    private long damaged;
    private long underMaintenance;
    private long lost;
    private Map<String, Long> byType;
    private Map<String, Long> byLocation;
    private Map<String, Long> byCompany;

    // ── Allocation Summary ────────────────────────────────────────────────────
    private long totalAllocations;
    private long activeAllocations;
    private long returnedAllocations;
    private long overdueAllocations;

    // ── Transfer Summary ──────────────────────────────────────────────────────
    private long totalTransfers;
    private long pendingTransfers;
    private long approvedTransfers;
    private long rejectedTransfers;

    // ── Disposal Summary ──────────────────────────────────────────────────────
    private long totalDisposals;
    private Map<String, Long> disposalsByMethod;
}
