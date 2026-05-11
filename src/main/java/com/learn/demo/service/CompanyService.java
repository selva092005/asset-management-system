package com.learn.demo.service;

import com.learn.demo.dto.request.CompanyRequestDTO;
import com.learn.demo.dto.response.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {
    CompanyResponseDTO saveCompany(CompanyRequestDTO dto);
    List<CompanyResponseDTO> getAllCompanies();
    CompanyResponseDTO getCompanyById(Long id);
    CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO dto);
    void deleteCompany(Long id);
}
