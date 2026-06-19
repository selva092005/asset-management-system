package com.learn.demo.service.ServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetRequest;
import com.learn.demo.model.AssetType;
import com.learn.demo.model.AssetMaintenance;
import com.learn.demo.model.User;
import com.learn.demo.dto.request.AssetRequestRequestDTO;
import com.learn.demo.dto.response.AssetRequestResponseDTO;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.repository.AssetRequestRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.repository.AssetTypeRepository;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.AssetMaintenanceRepository;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.service.AssetRequestService;
import com.learn.demo.service.AssetActivityLogService;
import com.learn.demo.service.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetRequestServiceImpl implements AssetRequestService {

    private final AssetRequestRepository requestRepository;
    private final AssetRepository assetRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetMaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;
    private final AssetActivityLogService activityLogService;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public AssetRequestResponseDTO createRequest(AssetRequestRequestDTO dto) {
        AssetRequest req = new AssetRequest();
        req.setRequestedBy(dto.getRequestedBy());
        req.setRequestType(dto.getRequestType().toUpperCase());
        req.setPriority(dto.getPriority().toUpperCase());
        req.setDescription(dto.getDescription());
        req.setRequestDate(LocalDate.now());
        req.setStatus("PENDING");
        req.setAttachmentPath(dto.getAttachmentPath());

        Asset asset = null;
        if (dto.getAssetId() != null) {
            asset = assetRepository.findById(dto.getAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));
            req.setAsset(asset);
        }

        if (dto.getTypeId() != null) {
            AssetType type = assetTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("AssetType not found with id: " + dto.getTypeId()));
            req.setAssetType(type);
        }

        AssetRequest saved = requestRepository.save(req);

        // Timeline log for existing asset
        if (asset != null) {
            activityLogService.log(asset, "REQUESTED", dto.getRequestedBy(),
                    String.format("Filed issue ticket of type %s with %s priority: %s",
                            dto.getRequestType(), dto.getPriority(), dto.getDescription()));
        }

        // Notify admins via WebSocket / real-time
        try {
            notificationService.notifyAdmins(String.format("New Ticket: '%s' request filed by %s (Priority: %s)",
                    req.getRequestType(), req.getRequestedBy(), req.getPriority()));
        } catch (Exception e) {
            System.err.println("WebSocket notification failed for request: " + e.getMessage());
        }

        return toDTO(saved);
    }

    @Override
    @Transactional
    public AssetRequestResponseDTO updateStatus(Long requestId, String status, String remarks, Double cost,
            String adminUser) {
        AssetRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("AssetRequest not found with id: " + requestId));

        String newStatus = status.toUpperCase();
        req.setStatus(newStatus);
        req.setRemarks(remarks);

        Asset asset = req.getAsset();

        // ── Business Rules on Status Transition ──
        if ("REPAIR".equals(req.getRequestType())) {
            if (asset != null) {
                if ("IN_PROGRESS".equals(newStatus)) {
                    asset.setStatus("UNDER_MAINTENANCE");
                    asset.setAssetCondition("POOR");
                    assetRepository.save(asset);
                    activityLogService.log(asset, "MAINTENANCE_START", adminUser,
                            "Repair ticket marked in progress. Admin remarks: " + remarks);
                } else if ("RESOLVED".equals(newStatus)) {
                    asset.setStatus("AVAILABLE");
                    asset.setAssetCondition("GOOD");
                    assetRepository.save(asset);
                    activityLogService.log(asset, "MAINTENANCE_END", adminUser, "Repair ticket marked resolved. Cost: ₹"
                            + (cost != null ? cost : 0.0) + ". Admin remarks: " + remarks);

                    // Auto-generate AssetMaintenance record
                    AssetMaintenance maintenance = new AssetMaintenance();
                    maintenance.setAsset(asset);
                    maintenance.setVendor("Internal IT Support");
                    maintenance.setCost(cost != null ? cost : 0.0); // Save the provided cost
                    maintenance.setStartDate(req.getRequestDate());
                    maintenance.setEndDate(LocalDate.now());
                    maintenance.setOutcome("FIXED");
                    maintenance.setRemarks(remarks);
                    maintenanceRepository.save(maintenance);
                }
            }
        } else if ("LOST".equals(req.getRequestType())) {
            if (asset != null && ("RESOLVED".equals(newStatus) || "APPROVED".equals(newStatus))) {
                asset.setStatus("LOST");
                asset.setAssetCondition("POOR");
                assetRepository.save(asset);
                activityLogService.log(asset, "LOST", adminUser, "Asset reported as lost. Admin remarks: " + remarks);
            }
        }

        AssetRequest saved = requestRepository.save(req);

        // ── Notify Requester ──
        try {
            String requesterEmail = null;
            User user = userRepository.findByUserNameAndDeletedFalse(req.getRequestedBy()).orElse(null);
            if (user != null) {
                requesterEmail = user.getUserEmail();
            } else {
                user = userRepository.findByUserEmailAndDeletedFalse(req.getRequestedBy()).orElse(null);
                if (user != null)
                    requesterEmail = user.getUserEmail();
            }

            if (requesterEmail != null) {
                notificationService.sendNotification(
                        String.format("Your ticket request #%d (%s) status has been updated to %s.",
                                req.getRequestId(), req.getRequestType(), newStatus),
                        requesterEmail);
            }
        } catch (Exception ex) {
            System.err.println("Failed to notify requester: " + ex.getMessage());
        }

        return toDTO(saved);
    }

    @Override
    public Page<AssetRequestResponseDTO> getRequests(String search, String status, String priority, String requestType,
            String username, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Count Query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<AssetRequest> countRoot = countQuery.from(AssetRequest.class);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, search, status, priority, requestType,
                username);
        countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        // Data Query
        CriteriaQuery<AssetRequest> query = cb.createQuery(AssetRequest.class);
        Root<AssetRequest> root = query.from(AssetRequest.class);
        List<Predicate> predicates = buildPredicates(cb, root, search, status, priority, requestType, username);
        query.where(predicates.toArray(new Predicate[0]));

        // Order by request date desc
        query.orderBy(cb.desc(root.get("requestDate")), cb.desc(root.get("createdAt")));

        TypedQuery<AssetRequest> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<AssetRequestResponseDTO> dtos = typedQuery.getResultList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, totalElements);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<AssetRequest> root, String search, String status,
            String priority, String requestType, String username) {
        List<Predicate> predicates = new ArrayList<>();
        Join<AssetRequest, Asset> assetJoin = root.join("asset", JoinType.LEFT);

        if (search != null && !search.isBlank()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("requestedBy")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern),
                    cb.like(cb.lower(assetJoin.get("assetName")), likePattern),
                    cb.like(cb.lower(assetJoin.get("assetCode")), likePattern)));
        }

        if (status != null && !status.isBlank()) {
            predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase()));
        }

        if (priority != null && !priority.isBlank()) {
            predicates.add(cb.equal(cb.upper(root.get("priority")), priority.toUpperCase()));
        }

        if (requestType != null && !requestType.isBlank()) {
            predicates.add(cb.equal(cb.upper(root.get("requestType")), requestType.toUpperCase()));
        }

        if (username != null && !username.isBlank()) {
            predicates.add(cb.equal(root.get("requestedBy"), username));
        }

        predicates.add(cb.equal(root.get("deleted"), false));

        return predicates;
    }

    @Override
    public Map<String, Long> getRequestOverview(String username) {
        Map<String, Long> stats = new HashMap<>();
        List<AssetRequest> allRequests = requestRepository.findAll().stream()
                .filter(r -> !r.isDeleted())
                .collect(Collectors.toList());

        if (username != null && !username.isBlank()) {
            allRequests = allRequests.stream()
                    .filter(r -> username.equalsIgnoreCase(r.getRequestedBy()))
                    .collect(Collectors.toList());
        }

        stats.put("total", (long) allRequests.size());
        stats.put("pending", allRequests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count());
        stats.put("inProgress",
                allRequests.stream().filter(r -> "IN_PROGRESS".equalsIgnoreCase(r.getStatus())).count());
        stats.put("resolved", allRequests.stream().filter(r -> "RESOLVED".equalsIgnoreCase(r.getStatus())).count());
        stats.put("rejected", allRequests.stream().filter(r -> "REJECTED".equalsIgnoreCase(r.getStatus())).count());

        return stats;
    }

    private AssetRequestResponseDTO toDTO(AssetRequest req) {
        AssetRequestResponseDTO dto = new AssetRequestResponseDTO();
        dto.setRequestId(req.getRequestId());
        dto.setRequestedBy(req.getRequestedBy());
        dto.setRequestType(req.getRequestType());
        dto.setPriority(req.getPriority());
        dto.setDescription(req.getDescription());
        dto.setStatus(req.getStatus());
        dto.setRemarks(req.getRemarks());
        dto.setRequestDate(req.getRequestDate());
        dto.setAttachmentPath(req.getAttachmentPath());

        if (req.getAsset() != null) {
            dto.setAssetId(req.getAsset().getAssetId());
            dto.setAssetName(req.getAsset().getAssetName());
            dto.setAssetCode(req.getAsset().getAssetCode());
        }

        if (req.getAssetType() != null) {
            dto.setTypeId(req.getAssetType().getTypeId());
            dto.setTypeName(req.getAssetType().getTypeName());
        }

        return dto;
    }
}
