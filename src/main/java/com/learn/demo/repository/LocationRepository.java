package com.learn.demo.repository;

import com.learn.demo.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCompany_CompanyId(Long companyId);
    Optional<Location> findByLocationNameIgnoreCase(String locationName);
}
