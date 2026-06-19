package com.learn.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.learn.demo.model.AssetRequest;

@Repository
public interface AssetRequestRepository extends JpaRepository<AssetRequest, Long>, JpaSpecificationExecutor<AssetRequest> {
    Page<AssetRequest> findByRequestedBy(String requestedBy, Pageable pageable);
    boolean existsByAsset_AssetIdAndStatusIn(Long assetId, java.util.Collection<String> statuses);
}
