package com.learn.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.learn.demo.model.AssetAudit;

@Repository
public interface AssetAuditRepository extends JpaRepository<AssetAudit, Long>, JpaSpecificationExecutor<AssetAudit> {
    List<AssetAudit> findByAsset_AssetIdOrderByAuditDateDesc(Long assetId);
}
