package com.learn.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.learn.demo.dto.request.AssetAllocationRequestDTO;
import com.learn.demo.dto.response.AllocationOverviewDTO;
import com.learn.demo.dto.response.AssetAllocationResponseDTO;

public interface AssetAllocationService {

    AssetAllocationResponseDTO allocate(AssetAllocationRequestDTO dto);

    AssetAllocationResponseDTO returnAsset(Long allocationId);

    // ── existing – no filters ─────────────────────────────────────────────────
    Page<AssetAllocationResponseDTO> getAllAllocations(Pageable pageable);

    // ── with optional filters ─────────────────────────────────────────────────
    Page<AssetAllocationResponseDTO> getAllAllocations(
            String search, String status,
            LocalDate fromDate, LocalDate toDate,
            Pageable pageable);

    // ── single allocation by id ───────────────────────────────────────────────
    AssetAllocationResponseDTO getById(Long id);

    List<AssetAllocationResponseDTO> getAllocationsByAsset(Long assetId);

    // ── overview stats ────────────────────────────────────────────────────────
    AllocationOverviewDTO getOverview();
}