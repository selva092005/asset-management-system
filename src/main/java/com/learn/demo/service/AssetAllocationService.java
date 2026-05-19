package com.learn.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.learn.demo.dto.request.AssetAllocationRequestDTO;
import com.learn.demo.dto.response.AssetAllocationResponseDTO;

public interface AssetAllocationService {

    AssetAllocationResponseDTO allocate(AssetAllocationRequestDTO dto);

    AssetAllocationResponseDTO returnAsset(Long allocationId);

    Page<AssetAllocationResponseDTO> getAllAllocations(Pageable pageable);

    List<AssetAllocationResponseDTO> getAllocationsByAsset(Long assetId);
}
