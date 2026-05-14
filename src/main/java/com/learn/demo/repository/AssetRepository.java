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

    /**
     * Find the MAX numeric suffix for a given prefix (e.g. "H-CH-IT-").
     * Uses native SQL to bypass @SQLRestriction so deleted assets are included,
     * preventing duplicate asset codes after soft-deletes.
     *
     * MySQL: CAST(SUBSTR(...) AS UNSIGNED) extracts the trailing number.
     * Returns 0 if no matching row exists (COALESCE).
     */
    @Query(value = """
        SELECT COALESCE(
            MAX(CAST(SUBSTR(asset_code, :prefixLen + 1) AS UNSIGNED)),
            0
        )
        FROM asset
        WHERE asset_code LIKE CONCAT(:prefix, '%')
        """, nativeQuery = true)
    long findMaxSequenceByPrefix(
        @Param("prefix") String prefix,
        @Param("prefixLen") int prefixLen
    );
}
