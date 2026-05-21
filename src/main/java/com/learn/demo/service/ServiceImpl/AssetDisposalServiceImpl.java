package com.learn.demo.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.dto.request.AssetDisposalRequestDTO;
import com.learn.demo.dto.response.AssetDisposalResponseDTO;
import com.learn.demo.exception.BusinessRuleException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetDisposal;
import com.learn.demo.repository.AssetDisposalRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetDisposalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetDisposalServiceImpl implements AssetDisposalService {

    private final AssetDisposalRepository disposalRepository;
    private final AssetRepository assetRepository;

    @Override
    @Transactional
    public AssetDisposalResponseDTO dispose(AssetDisposalRequestDTO dto) {

        Asset asset = assetRepository.findById(dto.getAssetId())
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        // Only AVAILABLE or DAMAGED assets can be disposed
        if (!"AVAILABLE".equalsIgnoreCase(asset.getStatus()) &&
                !"DAMAGED".equalsIgnoreCase(asset.getStatus())) {
            throw new BusinessRuleException(
                "Only AVAILABLE or DAMAGED assets can be disposed. Current status: " + asset.getStatus()
            );
        }

        // Mark asset as DISPOSED
        asset.setStatus("DISPOSED");
        assetRepository.save(asset);

        // Create disposal record
        AssetDisposal disposal = new AssetDisposal();
        disposal.setAsset(asset);
        disposal.setDisposalDate(dto.getDisposalDate());
        disposal.setDisposalMethod(dto.getDisposalMethod());
        disposal.setReason(dto.getReason());
        disposal.setDisposedBy(dto.getDisposedBy());
        disposal.setDisposalValue(dto.getDisposalValue());

        return toDTO(disposalRepository.save(disposal));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetDisposalResponseDTO> getAllDisposals() {
        return disposalRepository.findAllByOrderByDisposalDateDesc()
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssetDisposalResponseDTO getDisposalById(Long disposalId) {
        AssetDisposal disposal = disposalRepository.findById(disposalId)
            .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found with id: " + disposalId));
        return toDTO(disposal);
    }

    private AssetDisposalResponseDTO toDTO(AssetDisposal d) {
        AssetDisposalResponseDTO dto = new AssetDisposalResponseDTO();
        dto.setDisposalId(d.getDisposalId());
        dto.setAssetId(d.getAsset() != null ? d.getAsset().getAssetId() : null);
        dto.setAssetName(d.getAsset() != null ? d.getAsset().getAssetName() : "[Deleted Asset]");
        dto.setAssetCode(d.getAsset() != null ? d.getAsset().getAssetCode() : "");
        dto.setDisposalDate(d.getDisposalDate());
        dto.setDisposalMethod(d.getDisposalMethod());
        dto.setReason(d.getReason());
        dto.setDisposedBy(d.getDisposedBy());
        dto.setDisposalValue(d.getDisposalValue());
        return dto;
    }
}