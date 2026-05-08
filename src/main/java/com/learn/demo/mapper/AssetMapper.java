package com.learn.demo.mapper;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetType;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    // RequestDTO → Entity
    public Asset toEntity(AssetRequestDTO dto, AssetType assetType) {
        Asset asset = new Asset();
        asset.setAssetName(dto.getAssetName());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setBrand(dto.getBrand());
        asset.setModel(dto.getModel());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setWarrantyExpiry(dto.getWarrantyExpiry());
        asset.setCost(dto.getCost());
        asset.setStatus(dto.getStatus());
        asset.setAssetCondition(dto.getAssetCondition());
        asset.setNotes(dto.getNotes());
        asset.setLocationName(dto.getLocationName());
        asset.setAssetType(assetType);
        return asset;
    }

    // Entity → ResponseDTO
    public AssetResponseDTO toResponseDTO(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setAssetId(asset.getAssetId());
        dto.setAssetName(asset.getAssetName());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setWarrantyExpiry(asset.getWarrantyExpiry());
        dto.setCost(asset.getCost());
        dto.setStatus(asset.getStatus());
        dto.setAssetCondition(asset.getAssetCondition());
        dto.setNotes(asset.getNotes());
        dto.setLocationName(asset.getLocationName());
        dto.setAssetCode(asset.getAssetCode());
        dto.setQrCode(asset.getQrCode());
        if (asset.getAssetType() != null) {
            dto.setTypeId(asset.getAssetType().getTypeId());       // ✅ ADDED
            dto.setTypeName(asset.getAssetType().getTypeName());
        }
        return dto;
    }

    // Apply updates from RequestDTO to existing Entity (for PUT)
    public void updateEntityFromDTO(AssetRequestDTO dto, Asset asset, AssetType assetType) {
        asset.setAssetName(dto.getAssetName());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setBrand(dto.getBrand());
        asset.setModel(dto.getModel());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setWarrantyExpiry(dto.getWarrantyExpiry());
        asset.setCost(dto.getCost());
        asset.setStatus(dto.getStatus());
        asset.setAssetCondition(dto.getAssetCondition());
        asset.setNotes(dto.getNotes());
        asset.setLocationName(dto.getLocationName());
        if (assetType != null) {
            asset.setAssetType(assetType);
        }
    }
}