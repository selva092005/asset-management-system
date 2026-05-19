package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.AssetDisposal;

public interface AssetDisposalRepository extends JpaRepository<AssetDisposal, Long> {

    List<AssetDisposal> findAllByOrderByDisposalDateDesc();

    boolean existsByAsset_AssetId(Long assetId);
}
