package com.learn.demo.service.ServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.learn.demo.model.AssetAllocation;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetAudit;
import com.learn.demo.dto.request.AssetAuditRequestDTO;
import com.learn.demo.dto.response.AssetAuditResponseDTO;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.repository.AssetAuditRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetAuditService;
import com.learn.demo.service.AssetActivityLogService;
import com.learn.demo.service.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetAuditServiceImpl implements AssetAuditService {

    private final AssetAuditRepository auditRepository;
    private final AssetRepository assetRepository;
    private final AssetAllocationRepository allocationRepository;
    private final AssetActivityLogService activityLogService;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public AssetAuditResponseDTO createAudit(AssetAuditRequestDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        String auditStatus = dto.getStatus().toUpperCase();

        AssetAudit audit = new AssetAudit();
        audit.setAsset(asset);
        audit.setAuditedBy(dto.getAuditedBy());
        audit.setAuditDate(dto.getAuditDate() != null ? dto.getAuditDate() : LocalDate.now());
        audit.setStatus(auditStatus);
        audit.setRemarks(dto.getRemarks());
        audit.setActionTaken(dto.getActionTaken() != null ? dto.getActionTaken().toUpperCase() : "NONE");
        audit.setScreenOk(dto.getScreenOk() != null ? dto.getScreenOk() : true);
        audit.setKeyboardOk(dto.getKeyboardOk() != null ? dto.getKeyboardOk() : true);
        audit.setChargerOk(dto.getChargerOk() != null ? dto.getChargerOk() : true);
        audit.setBatteryOk(dto.getBatteryOk() != null ? dto.getBatteryOk() : true);

        AssetAudit saved = auditRepository.save(audit);

        // Propagate condition and status changes to the main Asset record
        if ("DAMAGED".equals(auditStatus)) {
            asset.setStatus("DAMAGED");
            asset.setAssetCondition("POOR");
            assetRepository.save(asset);
            triggerAdminAlert(asset, "DAMAGED", dto.getAuditedBy(), dto.getRemarks());
        } else if ("LOST".equals(auditStatus)) {
            asset.setStatus("LOST");
            asset.setAssetCondition("POOR");
            assetRepository.save(asset);
            triggerAdminAlert(asset, "LOST", dto.getAuditedBy(), dto.getRemarks());
            
            // Auto-terminate active allocation for lost asset
            closeActiveAllocation(asset.getAssetId(), "LOST");
        } else if ("GOOD".equals(auditStatus)) {
            boolean allOk = audit.getScreenOk() && audit.getKeyboardOk() && audit.getChargerOk() && audit.getBatteryOk();
            if (allOk) {
                asset.setAssetCondition("GOOD");
            } else {
                asset.setAssetCondition("FAIR");
            }
            if ("DAMAGED".equals(asset.getStatus()) || "LOST".equals(asset.getStatus()) || "UNDER_MAINTENANCE".equals(asset.getStatus())) {
                asset.setStatus("AVAILABLE");
            }
            assetRepository.save(asset);
        }

        // Write to unified activity log
        String typeName = asset.getAssetType() != null ? asset.getAssetType().getTypeName() : null;
        String logDetails = formatLogDetails(audit, typeName, dto.getRemarks());
        activityLogService.log(asset, "AUDITED", dto.getAuditedBy(), logDetails);

        return toDTO(saved);
    }

    private void closeActiveAllocation(Long assetId, String condition) {
        try {
            List<AssetAllocation> activeAllocs = allocationRepository.findByAsset_AssetIdOrderByAssignedDateDesc(assetId)
                .stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());
            for (AssetAllocation allocation : activeAllocs) {
                allocation.setStatus("RETURNED");
                allocation.setReturnDate(LocalDate.now());
                allocation.setRemarks("Auto-returned during physical audit: Asset flagged as " + condition);
                allocationRepository.save(allocation);
            }
        } catch (Exception ex) {
            System.err.println("Failed to auto-close active allocation for lost asset: " + ex.getMessage());
        }
    }

    private void triggerAdminAlert(Asset asset, String condition, String auditor, String remarks) {
        try {
            String alertMessage = String.format("AUDIT ALERT: Asset '%s' (%s) was flagged as %s by %s during audit. Remarks: %s",
                    asset.getAssetName(), asset.getAssetCode(), condition, auditor, remarks != null ? remarks : "None");
            
            // Send WebSocket/In-app notification
            notificationService.notifyAdmins(alertMessage);

            // Send notification to assigned user if the asset is currently allocated
            // Note: If allocated, get assigned details
        } catch (Exception ex) {
            // Log & ignore to prevent transactional rollbacks
            System.err.println("Failed to send audit alert: " + ex.getMessage());
        }
    }

    @Override
    public List<AssetAuditResponseDTO> getAudits(String search, String status, LocalDate fromDate, LocalDate toDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AssetAudit> query = cb.createQuery(AssetAudit.class);
        Root<AssetAudit> root = query.from(AssetAudit.class);
        Join<AssetAudit, Asset> assetJoin = root.join("asset", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(assetJoin.get("assetName")), likePattern),
                    cb.like(cb.lower(assetJoin.get("assetCode")), likePattern),
                    cb.like(cb.lower(root.get("auditedBy")), likePattern),
                    cb.like(cb.lower(root.get("remarks")), likePattern)
            ));
        }

        if (status != null && !status.isBlank()) {
            predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase()));
        }

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("auditDate"), fromDate));
        }

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("auditDate"), toDate));
        }

        // Only query records where deleted = false
        predicates.add(cb.equal(root.get("deleted"), false));

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("auditDate")), cb.desc(root.get("createdAt")));

        return entityManager.createQuery(query).getResultList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetAuditResponseDTO> getAuditsByAsset(Long assetId) {
        return auditRepository.findByAsset_AssetIdOrderByAuditDateDesc(assetId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getAuditOverview() {
        Map<String, Long> overview = new HashMap<>();
        
        // Fetch all non-deleted audits
        List<AssetAudit> audits = auditRepository.findAll().stream()
                .filter(a -> !a.isDeleted())
                .collect(Collectors.toList());
        
        overview.put("totalAudits", (long) audits.size());
        overview.put("goodCount", audits.stream().filter(a -> "GOOD".equalsIgnoreCase(a.getStatus())).count());
        overview.put("damagedCount", audits.stream().filter(a -> "DAMAGED".equalsIgnoreCase(a.getStatus())).count());
        overview.put("lostCount", audits.stream().filter(a -> "LOST".equalsIgnoreCase(a.getStatus())).count());
        
        return overview;
    }

    private String formatLogDetails(AssetAudit audit, String typeName, String remarks) {
        String type = (typeName != null ? typeName : "").toUpperCase();
        String screenLabel = "Screen";
        String keyboardLabel = "Keyboard";
        String chargerLabel = "Charger";
        String batteryLabel = "Battery";

        if (type.contains("FURNITURE") || type.contains("CHAIR") || type.contains("TABLE") || type.contains("DESK")) {
            screenLabel = "Structure";
            keyboardLabel = "Stability";
            chargerLabel = "Surface";
            batteryLabel = "Cleanliness";
        } else if (type.contains("MOBILE") || type.contains("PHONE") || type.contains("TABLET")) {
            screenLabel = "Screen";
            keyboardLabel = "Touchscreen/Buttons";
            chargerLabel = "Charger";
            batteryLabel = "Battery";
        } else if (!type.contains("IT") && !type.contains("LAPTOP") && !type.contains("COMPUTER") && !type.isEmpty()) {
            screenLabel = "Power/Wiring";
            keyboardLabel = "Outer Casing";
            chargerLabel = "Controls";
            batteryLabel = "Functions";
        }

        return String.format("Asset condition audited. %s: %s, %s: %s, %s: %s, %s: %s. Remarks: %s",
                screenLabel, audit.getScreenOk() ? "OK" : "Issue",
                keyboardLabel, audit.getKeyboardOk() ? "OK" : "Issue",
                chargerLabel, audit.getChargerOk() ? "OK" : "Issue",
                batteryLabel, audit.getBatteryOk() ? "OK" : "Issue",
                remarks != null ? remarks : "None");
    }

    private AssetAuditResponseDTO toDTO(AssetAudit audit) {
        AssetAuditResponseDTO dto = new AssetAuditResponseDTO();
        dto.setAuditId(audit.getAuditId());
        dto.setAssetId(audit.getAsset().getAssetId());
        dto.setAssetName(audit.getAsset().getAssetName());
        dto.setAssetCode(audit.getAsset().getAssetCode());
        if (audit.getAsset().getAssetType() != null) {
            dto.setTypeName(audit.getAsset().getAssetType().getTypeName());
        }
        dto.setAuditedBy(audit.getAuditedBy());
        dto.setAuditDate(audit.getAuditDate());
        dto.setStatus(audit.getStatus());
        dto.setRemarks(audit.getRemarks());
        dto.setActionTaken(audit.getActionTaken());
        dto.setScreenOk(audit.getScreenOk());
        dto.setKeyboardOk(audit.getKeyboardOk());
        dto.setChargerOk(audit.getChargerOk());
        dto.setBatteryOk(audit.getBatteryOk());
        return dto;
    }
}
