package com.learn.demo.service.ServiceImpl;

import com.learn.demo.dto.request.CompanyRequestDTO;
import com.learn.demo.dto.response.CompanyResponseDTO;
import com.learn.demo.exception.DuplicateResourceException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Company;
import com.learn.demo.repository.CompanyRepository;
import com.learn.demo.service.CompanyService;
import com.learn.demo.util.AssetCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;

    @Override
    public CompanyResponseDTO saveCompany(CompanyRequestDTO dto) {
        if (repository.existsByCompanyNameIgnoreCase(dto.getCompanyName())) {
            throw new DuplicateResourceException("Company", "name", dto.getCompanyName());
        }
        Company company = new Company();
        company.setCompanyName(dto.getCompanyName());
        return toResponse(repository.save(company));
    }

    @Override
    public List<CompanyResponseDTO> getAllCompanies() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDTO getCompanyById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id)));
    }

    @Override
    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO dto) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
        company.setCompanyName(dto.getCompanyName());
        return toResponse(repository.save(company));
    }

    @Override
    public void deleteCompany(Long id) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
        company.setDeleted(true);
        repository.save(company);
    }

    private CompanyResponseDTO toResponse(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCompanyName(company.getCompanyName());
        dto.setCompanyCode(AssetCodeGenerator.getCompanyCode(company.getCompanyName()));
        return dto;
    }
}
