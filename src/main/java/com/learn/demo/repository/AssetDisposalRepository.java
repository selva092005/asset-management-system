package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.learn.demo.model.AssetDisposal;

public interface AssetDisposalRepository extends JpaRepository<AssetDisposal, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<AssetDisposal> {

    List<AssetDisposal> findAllByOrderByDisposalDateDesc();

    boolean existsByAsset_AssetId(Long assetId);

    @Query("SELECT d.disposalMethod, COUNT(d) FROM AssetDisposal d GROUP BY d.disposalMethod")
    List<Object[]> countGroupByMethod();
}
