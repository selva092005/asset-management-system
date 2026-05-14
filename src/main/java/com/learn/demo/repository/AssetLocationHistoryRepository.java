package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.AssetLocationHistory;

public interface AssetLocationHistoryRepository extends JpaRepository<AssetLocationHistory, Long> {

    // Get full history for one asset, newest first
    List<AssetLocationHistory> findByAsset_AssetIdOrderByMovedAtDesc(Long assetId);
}
