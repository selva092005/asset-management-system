package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.AssetTransfer;

public interface AssetTransferRepository extends JpaRepository<AssetTransfer, Long> {

    Page<AssetTransfer> findAllByOrderByRequestedAtDesc(Pageable pageable);

    Page<AssetTransfer> findByStatusOrderByRequestedAtDesc(String status, Pageable pageable);

    List<AssetTransfer> findByAsset_AssetIdOrderByRequestedAtDesc(Long assetId);

    long countByStatus(String status);

    boolean existsByAsset_AssetIdAndStatus(Long assetId, String status);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM AssetTransfer t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(cast(:startDate as timestamp) IS NULL OR t.requestedAt >= :startDate) AND " +
            "(cast(:endDate as timestamp) IS NULL OR t.requestedAt <= :endDate)")
    Page<AssetTransfer> findByFilters(
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
}
