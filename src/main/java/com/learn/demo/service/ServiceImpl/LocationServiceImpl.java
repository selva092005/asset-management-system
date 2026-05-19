package com.learn.demo.service.ServiceImpl;

import com.learn.demo.dto.request.LocationRequestDTO;
import com.learn.demo.dto.response.LocationResponseDTO;
import com.learn.demo.exception.DuplicateResourceException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Company;
import com.learn.demo.model.Location;
import com.learn.demo.repository.CompanyRepository;
import com.learn.demo.repository.LocationRepository;
import com.learn.demo.service.LocationService;
import com.learn.demo.util.AssetCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;
    private final CompanyRepository companyRepository;

    @Override
    public LocationResponseDTO saveLocation(LocationRequestDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", dto.getCompanyId()));

        // Prevent duplicate location name within the same company
        if (repository.existsByLocationNameIgnoreCaseAndCompany_CompanyId(
                dto.getLocationName(), dto.getCompanyId())) {
            throw new DuplicateResourceException("Location", "name", dto.getLocationName());
        }

        Location location = new Location();
        location.setLocationName(dto.getLocationName());
        location.setCompany(company);
        return toResponse(repository.save(location));
    }

    @Override
    public List<LocationResponseDTO> getAllLocations() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationResponseDTO> getLocationsByCompany(Long companyId) {
        return repository.findByCompany_CompanyId(companyId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LocationResponseDTO getLocationById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id)));
    }

    @Override
    public LocationResponseDTO updateLocation(Long id, LocationRequestDTO dto) {
        Location location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", dto.getCompanyId()));

        // Check for duplicate only if the name is actually changing
        boolean nameChanged = !location.getLocationName().equalsIgnoreCase(dto.getLocationName());
        boolean companyChanged = !location.getCompany().getCompanyId().equals(dto.getCompanyId());

        if ((nameChanged || companyChanged) &&
            repository.existsByLocationNameIgnoreCaseAndCompany_CompanyId(
                    dto.getLocationName(), dto.getCompanyId())) {
            throw new DuplicateResourceException("Location", "name", dto.getLocationName());
        }

        location.setLocationName(dto.getLocationName());
        location.setCompany(company);
        return toResponse(repository.save(location));
    }

    @Override
    public void deleteLocation(Long id, String adminName) {
        Location location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));
        location.setDeleted(true);
        location.setDeletedBy(adminName);
        location.setDeletedAt(java.time.LocalDateTime.now());
        repository.save(location);
    }

    private LocationResponseDTO toResponse(Location location) {
        LocationResponseDTO dto = new LocationResponseDTO();
        dto.setLocationId(location.getLocationId());
        dto.setLocationName(location.getLocationName());
        dto.setLocationCode(AssetCodeGenerator.getLocationCode(location.getLocationName()));
        dto.setCompanyId(location.getCompany().getCompanyId());
        dto.setCompanyName(location.getCompany().getCompanyName());
        return dto;
    }
}
