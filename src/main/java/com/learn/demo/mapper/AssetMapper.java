package com.learn.demo.mapper;

import org.springframework.stereotype.Component;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetType;

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
        asset.setCompanyName(dto.getCompanyName());
        asset.setImagePath(dto.getImagePath());
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
        dto.setCompanyName(asset.getCompanyName());
        dto.setAssetCode(asset.getAssetCode());
        dto.setQrCode(asset.getQrCode());
        dto.setImagePath(asset.getImagePath());
        dto.setCreatedAt(asset.getCreatedAt());
        dto.setUpdatedAt(asset.getUpdatedAt());
        if (asset.getAssetType() != null) {
            dto.setTypeId(asset.getAssetType().getTypeId());
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
        asset.setCompanyName(dto.getCompanyName());
        if (dto.getImagePath() != null) {
            asset.setImagePath(dto.getImagePath());
        }
        if (assetType != null) {
            asset.setAssetType(assetType);
        }
    }
}