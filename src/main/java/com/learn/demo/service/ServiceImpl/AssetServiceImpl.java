package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.mapper.AssetMapper;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetType;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.repository.AssetTypeRepository;
import com.learn.demo.service.AssetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository repository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetMapper assetMapper;

    @Override
    public AssetResponseDTO saveAsset(AssetRequestDTO dto) {
        AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));
        Asset asset = assetMapper.toEntity(dto, assetType);
        return assetMapper.toResponseDTO(repository.save(asset));
    }

    @Override
    public List<AssetResponseDTO> saveAllAssets(List<AssetRequestDTO> dtos) {
        List<Asset> assets = dtos.stream()
                .map(dto -> {
                    AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));
                    return assetMapper.toEntity(dto, assetType);
                })
                .collect(Collectors.toList());

        return repository.saveAll(assets)
                .stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetResponseDTO> getAllAssets() {
        return repository.findAll()
                .stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetResponseDTO getAssetById(Long assetId) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        return assetMapper.toResponseDTO(asset);
    }

    @Override
    public AssetResponseDTO updateAsset(Long assetId, AssetRequestDTO dto) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        AssetType assetType = null;
        if (dto.getTypeId() != null) {
            assetType = assetTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));
        }

        assetMapper.updateEntityFromDTO(dto, asset, assetType);
        return assetMapper.toResponseDTO(repository.save(asset));
    }

    @Override
    public void deleteAsset(Long assetId, String adminName) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        asset.setDeleted(true);
        asset.setDeletedBy(adminName);
        asset.setDeletedAt(LocalDateTime.now());
        repository.save(asset);
    }

    @Override
    public Page<AssetResponseDTO> searchAssets(String name, String type, String location, Pageable pageable) {
        return repository.searchAssets(name, type, location, pageable)
                .map(assetMapper::toResponseDTO);
    }
}
