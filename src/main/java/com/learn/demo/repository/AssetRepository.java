package com.learn.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.learn.demo.model.Asset;


@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

@Query("SELECT a FROM Asset a WHERE " +
       "(:name IS NULL OR LOWER(a.assetName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
       "(:type IS NULL OR a.assetType.typeName = :type) AND " +
       "(:location IS NULL OR a.locationName = :location)")
Page<Asset> searchAssets(@Param("name") String name,
                         @Param("type") String type,
                         @Param("location") String location,
                         Pageable pageable);
    
}
