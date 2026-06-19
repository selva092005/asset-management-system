package com.learn.demo.mapper;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetType;
import com.learn.demo.model.Location;

@Component
@RequiredArgsConstructor
public class AssetMapper {

    // RequestDTO → Entity
    public Asset toEntity(AssetRequestDTO dto, AssetType assetType, Location location) {
        Asset asset = new Asset();
        asset.setAssetName(dto.getAssetName());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setBrand(dto.getBrand());
        asset.setModel(dto.getModel());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setWarrantyExpiry(dto.getWarrantyExpiry());
        asset.setCost(dto.getCost());
        asset.setDepreciationRate(dto.getDepreciationRate() != null ? dto.getDepreciationRate() : 20.0);
        asset.setStatus(dto.getStatus());
        asset.setAssetCondition(dto.getAssetCondition());
        asset.setNotes(dto.getNotes());
        asset.setLocation(location);
        asset.setCompany(location != null ? location.getCompany() : null);
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
        dto.setDepreciationRate(asset.getDepreciationRate() != null ? asset.getDepreciationRate() : 20.0);
        
        double currentVal = 0.0;
        if (asset.getCost() != null) {
            double purchasePrice = asset.getCost();
            double depRate = asset.getDepreciationRate() != null ? asset.getDepreciationRate() : 20.0;
            double yearsElapsed = 0.0;
            if (asset.getPurchaseDate() != null) {
                java.time.Period period = java.time.Period.between(asset.getPurchaseDate(), java.time.LocalDate.now());
                yearsElapsed = period.getYears() + (period.getMonths() / 12.0) + (period.getDays() / 365.25);
            }
            currentVal = purchasePrice - (yearsElapsed * (purchasePrice * (depRate / 100.0)));
            double minSalvage = purchasePrice * 0.1;
            if (currentVal < minSalvage) {
                currentVal = minSalvage;
            }
        }
        dto.setCurrentValue(currentVal);

        dto.setStatus(asset.getStatus());
        dto.setAssetCondition(asset.getAssetCondition());
        dto.setNotes(asset.getNotes());
        
        if (asset.getLocation() != null) {
            dto.setLocationId(asset.getLocation().getLocationId());
            dto.setLocationName(asset.getLocation().getLocationName());
            dto.setLatitude(asset.getLocation().getLatitude());
            dto.setLongitude(asset.getLocation().getLongitude());
        }
        
        if (asset.getCompany() != null) {
            dto.setCompanyName(asset.getCompany().getCompanyName());
        } else if (asset.getLocation() != null && asset.getLocation().getCompany() != null) {
            dto.setCompanyName(asset.getLocation().getCompany().getCompanyName());
        }
        
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
    public void updateEntityFromDTO(AssetRequestDTO dto, Asset asset, AssetType assetType, Location location) {
        asset.setAssetName(dto.getAssetName());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setBrand(dto.getBrand());
        asset.setModel(dto.getModel());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setWarrantyExpiry(dto.getWarrantyExpiry());
        asset.setCost(dto.getCost());
        if (dto.getDepreciationRate() != null) {
            asset.setDepreciationRate(dto.getDepreciationRate());
        }
        asset.setStatus(dto.getStatus());
        asset.setAssetCondition(dto.getAssetCondition());
        asset.setNotes(dto.getNotes());
        if (location != null) {
            asset.setLocation(location);
            asset.setCompany(location.getCompany());
        }
        if (dto.getImagePath() != null) {
            asset.setImagePath(dto.getImagePath());
        }
        if (assetType != null) {
            asset.setAssetType(assetType);
        }
    }
}