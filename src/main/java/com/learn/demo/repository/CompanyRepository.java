package com.learn.demo.repository;

import com.learn.demo.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyNameIgnoreCase(String companyName);
    boolean existsByCompanyNameIgnoreCase(String companyName);
}
