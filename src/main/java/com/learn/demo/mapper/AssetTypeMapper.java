package com.learn.demo.mapper;

import com.learn.demo.dto.request.AssetTypeRequestDTO;
import com.learn.demo.dto.response.AssetTypeResponseDTO;
import com.learn.demo.model.AssetType;
import org.springframework.stereotype.Component;

@Component
public class AssetTypeMapper {

    // RequestDTO → Entity
    public AssetType toEntity(AssetTypeRequestDTO dto) {
        AssetType assetType = new AssetType();
        assetType.setTypeName(dto.getTypeName());
        return assetType;
    }

    // Entity → ResponseDTO
    public AssetTypeResponseDTO toResponseDTO(AssetType assetType) {
        AssetTypeResponseDTO dto = new AssetTypeResponseDTO();
        dto.setTypeId(assetType.getTypeId());
        dto.setTypeName(assetType.getTypeName());
        return dto;
    }
}
