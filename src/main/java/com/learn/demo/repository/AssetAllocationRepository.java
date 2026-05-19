package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.AssetAllocation;

public interface AssetAllocationRepository extends JpaRepository<AssetAllocation, Long> {

    List<AssetAllocation> findByAsset_AssetIdOrderByAssignedDateDesc(Long assetId);

    boolean existsByAsset_AssetIdAndStatus(Long assetId, String status);

    List<AssetAllocation> findByStatusOrderByAssignedDateDesc(String status);

    Page<AssetAllocation> findAllByOrderByAssignedDateDesc(Pageable pageable);
}
