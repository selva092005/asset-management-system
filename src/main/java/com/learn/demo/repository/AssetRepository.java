package com.learn.demo.repository;

import com.learn.demo.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("""
    SELECT a FROM Asset a
    LEFT JOIN a.location loc
    WHERE a.deleted = false
    AND (:keyword IS NULL OR
         a.assetName LIKE %:keyword% OR
         a.assetCode LIKE %:keyword% OR
         a.serialNumber LIKE %:keyword% OR
         loc.locationName LIKE %:keyword%)
    AND (:type IS NULL OR a.assetType.typeName = :type)
    AND (:location IS NULL OR loc.locationName = :location)
    AND (:status IS NULL OR a.status = :status)
    """)
    Page<Asset> searchAssets(
        @Param("keyword") String keyword,
        @Param("type") String type,
        @Param("location") String location,
        @Param("status") String status,
        Pageable pageable
    );

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

    @Query("SELECT COUNT(a) > 0 FROM Asset a WHERE a.deleted = false AND a.serialNumber = :serialNumber")
    boolean existsBySerialNumber(@Param("serialNumber") String serialNumber);

    @Query("""
        SELECT COUNT(a) > 0 FROM Asset a
        WHERE a.deleted = false
        AND a.assetName = :assetName
        AND a.location.locationName = :locationName
    """)
    boolean existsByAssetNameAndLocationName(
        @Param("assetName") String assetName,
        @Param("locationName") String locationName
    );

    // ── DASHBOARD QUERIES ────────────────────────────────────────────────────

    List<Asset> findByDeletedFalse();

    long countByDeletedFalseAndStatus(String status);

    @Query("""
        SELECT COUNT(a) FROM Asset a
        WHERE a.deleted = false
        AND a.warrantyExpiry IS NOT NULL
        AND a.warrantyExpiry BETWEEN :today AND :cutoff
    """)
    long countExpiringWarranty(@Param("today") LocalDate today, @Param("cutoff") LocalDate cutoff);

    @Query("""
        SELECT a.assetType.typeName, COUNT(a) FROM Asset a
        WHERE a.deleted = false
        GROUP BY a.assetType.typeName
    """)
    List<Object[]> countGroupByType();

    @Query("""
        SELECT a.location.locationName, COUNT(a) FROM Asset a
        WHERE a.deleted = false
        GROUP BY a.location.locationName
    """)
    List<Object[]> countGroupByLocation();

    @Query("""
        SELECT a.location.company.companyName, COUNT(a) FROM Asset a
        WHERE a.deleted = false AND a.location.company.companyName IS NOT NULL
        GROUP BY a.location.company.companyName
    """)
    List<Object[]> countGroupByCompany();
}
