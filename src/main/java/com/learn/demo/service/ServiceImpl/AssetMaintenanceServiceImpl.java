package com.learn.demo.service.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetMaintenance;
import com.learn.demo.dto.response.AssetMaintenanceDTO;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.repository.AssetMaintenanceRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetMaintenanceService;
import com.learn.demo.service.AssetActivityLogService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetMaintenanceServiceImpl implements AssetMaintenanceService {

    private final AssetMaintenanceRepository maintenanceRepository;
    private final AssetRepository assetRepository;
    private final AssetActivityLogService activityLogService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public AssetMaintenanceDTO logMaintenance(AssetMaintenanceDTO dto, String actionBy) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        AssetMaintenance maintenance = new AssetMaintenance();
        maintenance.setAsset(asset);
        maintenance.setVendor(dto.getVendor());
        maintenance.setCost(dto.getCost());
        maintenance.setStartDate(dto.getStartDate());
        maintenance.setEndDate(dto.getEndDate());
        maintenance.setOutcome(dto.getOutcome() != null ? dto.getOutcome().toUpperCase() : "ONGOING");
        maintenance.setRemarks(dto.getRemarks());

        AssetMaintenance saved = maintenanceRepository.save(maintenance);

        String outcomeUpper = saved.getOutcome();
        if ("REPAIRED".equals(outcomeUpper)) {
            asset.setStatus("AVAILABLE");
            asset.setAssetCondition("GOOD");
            assetRepository.save(asset);
        } else if ("WRITTEN_OFF".equals(outcomeUpper)) {
            asset.setStatus("DAMAGED");
            asset.setAssetCondition("POOR");
            asset.setNotes("Written off during maintenance: " + dto.getRemarks());
            assetRepository.save(asset);
        }

        // Add timeline record
        String details = String.format("Maintenance Logged: Vendor='%s', Cost=INR %.2f, Outcome='%s', Remarks='%s'",
                saved.getVendor(), saved.getCost(), saved.getOutcome(), saved.getRemarks() != null ? saved.getRemarks() : "None");
        activityLogService.log(asset, "REPAIRED", actionBy, details);

        return toDTO(saved);
    }

    @Override
    public List<AssetMaintenanceDTO> getMaintenanceByAsset(Long assetId) {
        return maintenanceRepository.findByAsset_AssetIdOrderByStartDateDesc(assetId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetMaintenanceDTO> getAllMaintenance(String search, String outcome) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AssetMaintenance> query = cb.createQuery(AssetMaintenance.class);
        Root<AssetMaintenance> root = query.from(AssetMaintenance.class);
        Join<AssetMaintenance, Asset> assetJoin = root.join("asset", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(assetJoin.get("assetName")), likePattern),
                    cb.like(cb.lower(assetJoin.get("assetCode")), likePattern),
                    cb.like(cb.lower(root.get("vendor")), likePattern),
                    cb.like(cb.lower(root.get("remarks")), likePattern)
            ));
        }

        if (outcome != null && !outcome.isBlank()) {
            predicates.add(cb.equal(cb.upper(root.get("outcome")), outcome.toUpperCase()));
        }

        predicates.add(cb.equal(root.get("deleted"), false));

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("startDate")), cb.desc(root.get("createdAt")));

        return entityManager.createQuery(query).getResultList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AssetMaintenanceDTO toDTO(AssetMaintenance m) {
        AssetMaintenanceDTO dto = new AssetMaintenanceDTO();
        dto.setMaintenanceId(m.getMaintenanceId());
        dto.setAssetId(m.getAsset().getAssetId());
        dto.setAssetName(m.getAsset().getAssetName());
        dto.setAssetCode(m.getAsset().getAssetCode());
        dto.setVendor(m.getVendor());
        dto.setCost(m.getCost());
        dto.setStartDate(m.getStartDate());
        dto.setEndDate(m.getEndDate());
        dto.setOutcome(m.getOutcome());
        dto.setRemarks(m.getRemarks());
        return dto;
    }
}
