package com.learn.demo.service.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

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

        // Only AVAILABLE, DAMAGED, or UNDER_MAINTENANCE assets can be disposed
        if (!"AVAILABLE".equalsIgnoreCase(asset.getStatus()) &&
                !"DAMAGED".equalsIgnoreCase(asset.getStatus()) &&
                !"UNDER_MAINTENANCE".equalsIgnoreCase(asset.getStatus())) {
            throw new BusinessRuleException(
                "Only AVAILABLE, DAMAGED or UNDER_MAINTENANCE assets can be disposed. Current status: " + asset.getStatus()
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
    public List<AssetDisposalResponseDTO> getAllDisposals(String search, String method) {
        Specification<AssetDisposal> spec = buildSpec(search, method);
        return disposalRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "disposalDate"))
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private Specification<AssetDisposal> buildSpec(String search, String method) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                Join<AssetDisposal, Asset> assetJoin = root.join("asset", JoinType.LEFT);
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("disposedBy")), like),
                    cb.like(cb.lower(root.get("reason")), like),
                    cb.like(cb.lower(assetJoin.get("assetName")), like),
                    cb.like(cb.lower(assetJoin.get("assetCode")), like)
                ));
            }

            if (method != null && !method.isBlank()) {
                predicates.add(cb.equal(root.get("disposalMethod"), method.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
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
        if (d.getAsset() != null) {
            dto.setAssetImagePath(d.getAsset().getImagePath());
        }
        return dto;
    }
}