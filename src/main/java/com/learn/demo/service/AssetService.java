package com.learn.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;

@Service
public interface AssetService {

    AssetResponseDTO saveAsset(AssetRequestDTO dto);

    List<AssetResponseDTO> saveAllAssets(List<AssetRequestDTO> dtos);

    List<AssetResponseDTO> getAllAssets();

    AssetResponseDTO getAssetById(Long assetId);

    AssetResponseDTO updateAsset(Long assetId, AssetRequestDTO dto);

    void deleteAsset(Long assetId, String adminName);

    Page<AssetResponseDTO> searchAssets(String name, String type, String location, Pageable pageable);
}
