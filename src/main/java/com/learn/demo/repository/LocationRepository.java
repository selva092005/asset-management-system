package com.learn.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.learn.demo.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

        List<Location> findByCompany_CompanyId(Long companyId);

        Optional<Location> findByLocationNameIgnoreCase(String locationName);

        Optional<Location> findByLocationNameIgnoreCaseAndCompany_CompanyNameIgnoreCase(String locationName,
                        String companyName);

        List<Location> findAllByLocationNameIgnoreCase(String locationName);

        // Used to prevent duplicate location names within the same company
        boolean existsByLocationNameIgnoreCaseAndCompany_CompanyId(String locationName, Long companyId);

        @org.springframework.data.jpa.repository.Query("SELECT l FROM Location l WHERE " +
                        "(:locationType IS NULL OR l.locationType = :locationType) AND " +
                        "(:search IS NULL OR " +
                        "LOWER(l.locationName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(l.company.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(l.locationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(l.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(l.contactPerson) LIKE LOWER(CONCAT('%', :search, '%')))")
        List<Location> searchLocations(
                        @Param("locationType") String locationType,
                        @Param("search") String search);
}
