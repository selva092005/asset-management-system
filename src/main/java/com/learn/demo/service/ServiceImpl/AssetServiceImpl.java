package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.learn.demo.util.AssetCodeGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository repository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetMapper assetMapper;

    // ─────────────────────────────────────────────
    // SINGLE SAVE
    // ─────────────────────────────────────────────
    @Override
    public AssetResponseDTO saveAsset(AssetRequestDTO dto) {
        AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));

        Asset asset = assetMapper.toEntity(dto, assetType);

        // ✅ Auto generate asset code: H-CH-IT-00001
        String assetCode = generateAssetCode(dto.getCompanyName(), dto.getLocationName(), assetType.getTypeName(), null);
        asset.setAssetCode(assetCode);

        // ✅ Auto generate QR code
        String qrContent = "http://localhost:5173/home/assets/" + assetCode;
        asset.setQrCode(AssetCodeGenerator.generateQrCodeBase64(qrContent));

        return assetMapper.toResponseDTO(repository.save(asset));
    }

    // ─────────────────────────────────────────────
    // BULK SAVE — fixed duplicate code bug
    // ─────────────────────────────────────────────
    @Override
    public List<AssetResponseDTO> saveAllAssets(List<AssetRequestDTO> dtos) {

        // track local count per prefix to avoid duplicates in bulk
        Map<String, Long> localCounterMap = new HashMap<>();

        List<Asset> assets = dtos.stream()
                .map(dto -> {
                    AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));

                    Asset asset = assetMapper.toEntity(dto, assetType);

                    // ✅ Pass localCounterMap to handle bulk duplicates
                    String assetCode = generateAssetCode(
                            dto.getCompanyName(),
                            dto.getLocationName(),
                            assetType.getTypeName(),
                            localCounterMap
                    );
                    asset.setAssetCode(assetCode);

                    // ✅ Auto generate QR code
                    String qrContent = "http://localhost:5173/home/assets/" + assetCode;
                    asset.setQrCode(AssetCodeGenerator.generateQrCodeBase64(qrContent));

                    return asset;
                })
                .collect(Collectors.toList());

        return repository.saveAll(assets)
                .stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // ASSET CODE GENERATION
    //
    // companyName  → "Hero"    → "H"
    // locationName → "Chennai" → "CH"
    // typeName     → "IT"      → "IT"
    // prefix       → "H-CH-IT-"
    // DB count     → 0 + 1 = 1
    // final code   → "H-CH-IT-00001"
    //
    // localCounterMap: used in bulk save to avoid duplicates
    // pass null for single save
    // ─────────────────────────────────────────────
    private String generateAssetCode(String companyName, String locationName, String typeName,
                                     Map<String, Long> localCounterMap) {

        String prefix = AssetCodeGenerator.buildPrefix(companyName, locationName, typeName);

        // get DB count for this prefix
        long dbCount = repository.countByAssetCodePrefix(prefix);

        long count;
        if (localCounterMap != null) {
            // bulk save — increment local counter per prefix
            localCounterMap.merge(prefix, 1L, Long::sum);
            count = dbCount + localCounterMap.get(prefix);
        } else {
            // single save
            count = dbCount + 1;
        }

        return String.format("%s%05d", prefix, count);
    }

    // ─────────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────────
    @Override
    public List<AssetResponseDTO> getAllAssets() {
        return repository.findAll()
                .stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    public AssetResponseDTO getAssetById(Long assetId) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        return assetMapper.toResponseDTO(asset);
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────
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

    // ─────────────────────────────────────────────
    // DELETE (soft delete)
    // ─────────────────────────────────────────────
    @Override
    public void deleteAsset(Long assetId, String adminName) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        asset.setDeleted(true);
        asset.setDeletedBy(adminName);
        asset.setDeletedAt(LocalDateTime.now());
        repository.save(asset);
    }

    // ─────────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────────
    @Override
    public Page<AssetResponseDTO> searchAssets(String name, String type, String location, Pageable pageable) {
        return repository.searchAssets(name, type, location, pageable)
                .map(assetMapper::toResponseDTO);
    }
}
