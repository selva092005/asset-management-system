package com.learn.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.learn.demo.model.AssetMaintenance;

@Repository
public interface AssetMaintenanceRepository extends JpaRepository<AssetMaintenance, Long>, JpaSpecificationExecutor<AssetMaintenance> {
    List<AssetMaintenance> findByAsset_AssetIdOrderByStartDateDesc(Long assetId);
}
