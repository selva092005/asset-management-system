package com.learn.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.learn.demo.model.AssetActivityLog;

@Repository
public interface AssetActivityLogRepository extends JpaRepository<AssetActivityLog, Long> {
    List<AssetActivityLog> findByAsset_AssetIdOrderByActionDateDesc(Long assetId);
}
