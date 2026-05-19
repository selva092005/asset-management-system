package com.learn.demo.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learn.demo.dto.request.CompanyRequestDTO;
import com.learn.demo.dto.response.CompanyResponseDTO;
import com.learn.demo.exception.DuplicateResourceException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Company;
import com.learn.demo.repository.CompanyRepository;
import com.learn.demo.service.CompanyService;
import com.learn.demo.util.AssetCodeGenerator;

import lombok.RequiredArgsConstructor;

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

    /**
     * Company name is intentionally locked after creation.
     * It is embedded in all asset codes (e.g. "H-CH-IT-00001"),
     * so renaming would break every existing asset code for that company.
     *
     * If you need to rename a company, create a new company and
     * re-assign assets manually.
     */
    @Override
    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO dto) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));

        // Block name change — company name is baked into asset codes
        if (!company.getCompanyName().equalsIgnoreCase(dto.getCompanyName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Company name cannot be changed after creation. " +
                "It is embedded in existing asset codes. " +
                "Create a new company instead."
            );
        }

        // Name is the same — nothing to update (extend here for future fields)
        return toResponse(company);
    }

    @Override
    public void deleteCompany(Long id, String adminName) {
        Company company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
        company.setDeleted(true);
        company.setDeletedBy(adminName);
        company.setDeletedAt(java.time.LocalDateTime.now());
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
