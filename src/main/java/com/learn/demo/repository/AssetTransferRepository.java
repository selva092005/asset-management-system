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
}
