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

        // Convert status and assetCondition to UPPERCASE
        asset.setStatus(dto.getStatus().toUpperCase());
        asset.setAssetCondition(dto.getAssetCondition() != null
                ? dto.getAssetCondition().toUpperCase() : null);

        // Auto-generate asset code using MAX sequence (safe after soft-deletes)
        String assetCode = generateAssetCode(dto.getCompanyName(), dto.getLocationName(), assetType.getTypeName(), null);
        asset.setAssetCode(assetCode);

        // Auto-generate QR code
        String qrContent = "http://localhost:5173/home/assets/" + assetCode;
        asset.setQrCode(AssetCodeGenerator.generateQrCodeBase64(qrContent));

        return assetMapper.toResponseDTO(repository.save(asset));
    }

    // ─────────────────────────────────────────────
    // BULK SAVE
    // ─────────────────────────────────────────────
    @Override
    public List<AssetResponseDTO> saveAllAssets(List<AssetRequestDTO> dtos) {

        // Track local offset per prefix to avoid duplicates within the same bulk batch
        Map<String, Long> localOffsetMap = new HashMap<>();

        List<Asset> assets = dtos.stream()
                .map(dto -> {
                    AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));

                    Asset asset = assetMapper.toEntity(dto, assetType);

                    // Convert status and assetCondition to UPPERCASE
                    asset.setStatus(dto.getStatus().toUpperCase());
                    asset.setAssetCondition(dto.getAssetCondition() != null
                            ? dto.getAssetCondition().toUpperCase() : null);

                    // Pass localOffsetMap to handle duplicates within the bulk batch
                    String assetCode = generateAssetCode(
                            dto.getCompanyName(),
                            dto.getLocationName(),
                            assetType.getTypeName(),
                            localOffsetMap
                    );
                    asset.setAssetCode(assetCode);

                    // Auto-generate QR code
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
    // Uses MAX sequence (not COUNT) so soft-deleted assets never
    // cause duplicate codes.
    // ─────────────────────────────────────────────
    private String generateAssetCode(String companyName, String locationName, String typeName,
                                     Map<String, Long> localOffsetMap) {

        String prefix = AssetCodeGenerator.buildPrefix(companyName, locationName, typeName);
        int prefixLen = prefix.length();

        // MAX of the trailing numeric part across ALL rows (including deleted)
        long maxSeq = repository.findMaxSequenceByPrefix(prefix, prefixLen);

        long nextSeq;
        if (localOffsetMap != null) {
            // Each item in the bulk batch bumps the local offset for this prefix
            localOffsetMap.merge(prefix, 1L, Long::sum);
            nextSeq = maxSeq + localOffsetMap.get(prefix);
        } else {
            nextSeq = maxSeq + 1;
        }

        return String.format("%s%05d", prefix, nextSeq);
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

        // Convert status and assetCondition to UPPERCASE on update too
        asset.setStatus(dto.getStatus().toUpperCase());
        asset.setAssetCondition(dto.getAssetCondition() != null
                ? dto.getAssetCondition().toUpperCase() : null);

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
