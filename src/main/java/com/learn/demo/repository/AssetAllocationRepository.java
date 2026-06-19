package com.learn.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.learn.demo.model.AssetAllocation;

public interface AssetAllocationRepository
        extends JpaRepository<AssetAllocation, Long>, JpaSpecificationExecutor<AssetAllocation> {

    // ── existing methods – keep as-is ─────────────────────────────────────────
    List<AssetAllocation> findByAsset_AssetIdOrderByAssignedDateDesc(Long assetId);

    boolean existsByAsset_AssetIdAndStatus(Long assetId, String status);

    boolean existsByAssignedToAndStatus(String assignedTo, String status);

    List<AssetAllocation> findByStatusOrderByAssignedDateDesc(String status);

    Page<AssetAllocation> findAllByOrderByAssignedDateDesc(Pageable pageable);

    // ── overview counts ───────────────────────────────────────────────────────
    long countByStatus(String status);

    // overdue: ACTIVE allocations whose expectedReturnDate is before today
    @Query("SELECT COUNT(a) FROM AssetAllocation a WHERE a.status = 'ACTIVE' AND a.expectedReturnDate < :today")
    long countOverdue(@Param("today") LocalDate today);

    @Query("SELECT a FROM AssetAllocation a JOIN FETCH a.asset WHERE a.status = 'ACTIVE' AND a.expectedReturnDate < :today")
    List<AssetAllocation> findOverdueAllocations(@Param("today") LocalDate today);

    // awaiting return: ACTIVE allocations whose expectedReturnDate >= today OR no date set
    @Query("SELECT COUNT(a) FROM AssetAllocation a WHERE a.status = 'ACTIVE' AND (a.expectedReturnDate IS NULL OR a.expectedReturnDate >= :today)")
    long countAwaitingReturn(@Param("today") LocalDate today);
}