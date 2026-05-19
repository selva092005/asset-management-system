package com.learn.demo.service.ServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.dto.request.AssetAllocationRequestDTO;
import com.learn.demo.dto.response.AssetAllocationResponseDTO;
import com.learn.demo.exception.BusinessRuleException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetAllocation;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetAllocationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetAllocationServiceImpl implements AssetAllocationService {

    private final AssetAllocationRepository allocationRepository;
    private final AssetRepository assetRepository;

    // ── ALLOCATE ──────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetAllocationResponseDTO allocate(AssetAllocationRequestDTO dto) {

        Asset asset = assetRepository.findById(dto.getAssetId())
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        if ("DISPOSED".equalsIgnoreCase(asset.getStatus()) || "DAMAGED".equalsIgnoreCase(asset.getStatus())) {
            throw new BusinessRuleException("Cannot allocate a disposed or damaged asset.");
        }

        if (allocationRepository.existsByAsset_AssetIdAndStatus(dto.getAssetId(), "ACTIVE")) {
            throw new BusinessRuleException("Asset is already allocated. Return it first.");
        }

        asset.setStatus("ASSIGNED");
        assetRepository.save(asset);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setAsset(asset);
        allocation.setAssignedTo(dto.getAssignedTo());
        allocation.setAssignedBy(dto.getAssignedBy());
        allocation.setAssignedDate(dto.getAssignedDate());
        allocation.setExpectedReturnDate(dto.getExpectedReturnDate());
        allocation.setRemarks(dto.getRemarks());
        allocation.setStatus("ACTIVE");

        return toDTO(allocationRepository.save(allocation));
    }

    // ── RETURN ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetAllocationResponseDTO returnAsset(Long allocationId) {

        AssetAllocation allocation = allocationRepository.findById(allocationId)
            .orElseThrow(() -> new ResourceNotFoundException("Allocation not found with id: " + allocationId));

        if ("RETURNED".equalsIgnoreCase(allocation.getStatus())) {
            throw new BusinessRuleException("Asset has already been returned.");
        }

        allocation.setStatus("RETURNED");
        allocation.setReturnDate(LocalDate.now());

        Asset asset = allocation.getAsset();
        if (!asset.isDeleted()) {
            asset.setStatus("AVAILABLE");
            assetRepository.save(asset);
        }

        return toDTO(allocationRepository.save(allocation));
    }

    // ── GET ALL (paginated) ───────────────────────────────────────────────────
    @Override
    public Page<AssetAllocationResponseDTO> getAllAllocations(Pageable pageable) {
        return allocationRepository.findAllByOrderByAssignedDateDesc(pageable)
            .map(this::toDTO);
    }

    // ── GET BY ASSET ──────────────────────────────────────────────────────────
    @Override
    public List<AssetAllocationResponseDTO> getAllocationsByAsset(Long assetId) {
        return allocationRepository.findByAsset_AssetIdOrderByAssignedDateDesc(assetId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────
    private AssetAllocationResponseDTO toDTO(AssetAllocation a) {
        AssetAllocationResponseDTO dto = new AssetAllocationResponseDTO();
        dto.setAllocationId(a.getAllocationId());
        dto.setAssetId(a.getAsset().getAssetId());
        dto.setAssetName(a.getAsset().getAssetName());
        dto.setAssetCode(a.getAsset().getAssetCode());
        dto.setLocationName(a.getAsset().getLocationName());
        dto.setAssignedTo(a.getAssignedTo());
        dto.setAssignedBy(a.getAssignedBy());
        dto.setAssignedDate(a.getAssignedDate());
        dto.setExpectedReturnDate(a.getExpectedReturnDate());
        dto.setReturnDate(a.getReturnDate());
        dto.setRemarks(a.getRemarks());
        dto.setStatus(a.getStatus());
        return dto;
    }
}
