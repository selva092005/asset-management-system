package com.learn.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCompany_CompanyId(Long companyId);

    Optional<Location> findByLocationNameIgnoreCase(String locationName);

    // Used to prevent duplicate location names within the same company
    boolean existsByLocationNameIgnoreCaseAndCompany_CompanyId(String locationName, Long companyId);
}
