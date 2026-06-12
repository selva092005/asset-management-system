package com.learn.demo.service;

import com.learn.demo.dto.request.LocationRequestDTO;
import com.learn.demo.dto.response.LocationResponseDTO;

import java.util.List;

public interface LocationService {
    LocationResponseDTO saveLocation(LocationRequestDTO dto);
    List<LocationResponseDTO> getAllLocations(String locationType, String search);
    List<LocationResponseDTO> getLocationsByCompany(Long companyId);
    LocationResponseDTO getLocationById(Long id);
    LocationResponseDTO updateLocation(Long id, LocationRequestDTO dto);
    void deleteLocation(Long id, String adminName);
}
