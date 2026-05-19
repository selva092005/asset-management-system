package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.dto.request.MoveAssetRequestDTO;
import com.learn.demo.dto.response.AssetLocationHistoryResponseDTO;
import com.learn.demo.exception.BusinessRuleException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetLocationHistory;
import com.learn.demo.repository.AssetLocationHistoryRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetLocationHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetLocationHistoryServiceImpl implements AssetLocationHistoryService {

    private final AssetRepository assetRepository;
    private final AssetLocationHistoryRepository historyRepository;

    // ─────────────────────────────────────────────
    // MOVE ASSET  (transaction: both saves succeed or both roll back)
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public AssetLocationHistoryResponseDTO moveAsset(MoveAssetRequestDTO dto) {

        // 1. Load the asset (throws 404 if not found)
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", dto.getAssetId()));

        // Block moving disposed or damaged assets
        String status = asset.getStatus();
        if ("DISPOSED".equalsIgnoreCase(status) || "DAMAGED".equalsIgnoreCase(status)) {
            throw new BusinessRuleException("Cannot move a " + status.toLowerCase() + " asset.");
        }

        if (dto.getNewLocation().equalsIgnoreCase(asset.getLocationName())) {
            throw new BusinessRuleException("Asset is already at location: " + dto.getNewLocation());
        }

        String oldLocation = asset.getLocationName();

        // 2. ① Save history row
        AssetLocationHistory history = new AssetLocationHistory();
        history.setAsset(asset);
        history.setFromLocation(oldLocation);
        history.setToLocation(dto.getNewLocation());
        history.setMovedBy(dto.getMovedBy());
        history.setMovedAt(LocalDateTime.now());
        history.setReason(dto.getReason());

        AssetLocationHistory saved = historyRepository.save(history);

        // 3. ② Update asset's current location
        asset.setLocationName(dto.getNewLocation());
        assetRepository.save(asset);

        // 4. Return the saved history record as a DTO
        return toResponseDTO(saved);
    }

    // ─────────────────────────────────────────────
    // GET HISTORY FOR ONE ASSET
    // ─────────────────────────────────────────────
    @Override
    public List<AssetLocationHistoryResponseDTO> getHistoryByAssetId(Long assetId) {

        // Confirm asset exists and is not soft-deleted
        assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        return historyRepository
                .findByAsset_AssetIdOrderByMovedAtDesc(assetId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // MAPPER  (kept private — no separate Mapper class needed)
    // ─────────────────────────────────────────────
    private AssetLocationHistoryResponseDTO toResponseDTO(AssetLocationHistory h) {
        AssetLocationHistoryResponseDTO dto = new AssetLocationHistoryResponseDTO();
        dto.setHistoryId(h.getHistoryId());
        dto.setAssetId(h.getAsset().getAssetId());
        dto.setAssetName(h.getAsset().getAssetName());
        dto.setAssetCode(h.getAsset().getAssetCode());
        dto.setFromLocation(h.getFromLocation());
        dto.setToLocation(h.getToLocation());
        dto.setMovedBy(h.getMovedBy());
        dto.setMovedAt(h.getMovedAt());
        dto.setReason(h.getReason());
        return dto;
    }
}
