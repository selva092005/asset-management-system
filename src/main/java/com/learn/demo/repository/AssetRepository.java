package com.learn.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.learn.demo.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("""
    SELECT a FROM Asset a
    WHERE a.deleted = false
    AND (:name IS NULL OR a.assetName LIKE %:name%)
    AND (:type IS NULL OR a.assetType.typeName = :type)
    AND (:location IS NULL OR a.locationName = :location)
    """)
    Page<Asset> searchAssets(
        @Param("name") String name,
        @Param("type") String type,
        @Param("location") String location,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(a) FROM Asset a WHERE a.assetCode LIKE :prefix%")
long countByAssetCodePrefix(@Param("prefix") String prefix);
}