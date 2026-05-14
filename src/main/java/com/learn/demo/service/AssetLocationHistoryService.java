package com.learn.demo.service;

import java.util.List;

import com.learn.demo.dto.request.MoveAssetRequestDTO;
import com.learn.demo.dto.response.AssetLocationHistoryResponseDTO;

public interface AssetLocationHistoryService {

    // Move an asset to a new location (saves history row + updates asset.locationName in one transaction)
    AssetLocationHistoryResponseDTO moveAsset(MoveAssetRequestDTO dto);

    // Get the full location history for a specific asset
    List<AssetLocationHistoryResponseDTO> getHistoryByAssetId(Long assetId);
}
