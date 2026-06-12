package com.learn.demo.service.ServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.dto.request.AssetAllocationRequestDTO;
import com.learn.demo.dto.response.AllocationOverviewDTO;
import com.learn.demo.dto.response.AssetAllocationResponseDTO;
import com.learn.demo.exception.BusinessRuleException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetAllocation;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetAllocationService;
import com.learn.demo.service.NotificationService;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.model.User;
import java.util.Optional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetAllocationServiceImpl implements AssetAllocationService {

    private final AssetAllocationRepository allocationRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ── ALLOCATE ──────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetAllocationResponseDTO allocate(AssetAllocationRequestDTO dto) {

        Asset asset = assetRepository.findById(dto.getAssetId())
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        if ("DISPOSED".equalsIgnoreCase(asset.getStatus()) || "DAMAGED".equalsIgnoreCase(asset.getStatus())) {
            throw new BusinessRuleException("Cannot allocate a disposed or damaged asset.");
        }

        if (allocationRepository.existsByAsset_AssetIdAndStatus(dto.getAssetId(), "ACTIVE")) {
            throw new BusinessRuleException("Asset is already allocated. Return it first.");
        }

        asset.setStatus("ASSIGNED");
        assetRepository.save(asset);

        AssetAllocation allocation = new AssetAllocation();
        allocation.setAsset(asset);
        allocation.setAssignedTo(dto.getAssignedTo());
        allocation.setAssignedBy(dto.getAssignedBy());
        allocation.setAssignedDate(dto.getAssignedDate());
        allocation.setExpectedReturnDate(dto.getExpectedReturnDate());
        allocation.setRemarks(dto.getRemarks());
        allocation.setStatus("ACTIVE");

        AssetAllocationResponseDTO response = toDTO(allocationRepository.save(allocation));

        try {
            String recipientEmail = null;
            Optional<User> targetUser = userRepository.findByUserNameAndDeletedFalse(dto.getAssignedTo());
            if (targetUser.isPresent()) {
                recipientEmail = targetUser.get().getUserEmail();
            } else {
                Optional<User> targetUserByEmail = userRepository.findByUserEmailAndDeletedFalse(dto.getAssignedTo());
                if (targetUserByEmail.isPresent()) {
                    recipientEmail = targetUserByEmail.get().getUserEmail();
                }
            }
            if (recipientEmail != null) {
                notificationService.sendNotification(
                    String.format("Asset '%s' (%s) has been allocated to you by %s.", asset.getAssetName(), asset.getAssetCode(), dto.getAssignedBy()),
                    recipientEmail
                );
                notificationService.sendAllocationEmail(
                    recipientEmail,
                    dto.getAssignedTo(),
                    asset.getAssetName(),
                    asset.getAssetCode(),
                    "ALLOCATED"
                );
            }
        } catch (Exception ex) {
            // Log or ignore to prevent breaking the transaction
        }

        return response;
    }

    // ── RETURN ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetAllocationResponseDTO returnAsset(Long allocationId, LocalDate returnDate) {

        AssetAllocation allocation = allocationRepository.findById(allocationId)
            .orElseThrow(() -> new ResourceNotFoundException("Allocation not found with id: " + allocationId));

        if ("RETURNED".equalsIgnoreCase(allocation.getStatus())) {
            throw new BusinessRuleException("Asset has already been returned.");
        }

        LocalDate finalReturnDate = returnDate != null ? returnDate : LocalDate.now();

        if (finalReturnDate.isBefore(allocation.getAssignedDate())) {
            throw new BusinessRuleException("Actual return date cannot be before the assigned date (" + allocation.getAssignedDate() + ").");
        }

        allocation.setStatus("RETURNED");
        allocation.setReturnDate(finalReturnDate);

        Asset asset = allocation.getAsset();
        if (!asset.isDeleted()) {
            asset.setStatus("AVAILABLE");
            assetRepository.save(asset);
        }

        AssetAllocationResponseDTO response = toDTO(allocationRepository.save(allocation));

        try {
            notificationService.notifyAdmins(
                String.format("Asset '%s' (%s) has been returned by %s.", asset.getAssetName(), asset.getAssetCode(), allocation.getAssignedTo())
            );

            String recipientEmail = null;
            Optional<User> targetUser = userRepository.findByUserNameAndDeletedFalse(allocation.getAssignedTo());
            if (targetUser.isPresent()) {
                recipientEmail = targetUser.get().getUserEmail();
            } else {
                Optional<User> targetUserByEmail = userRepository.findByUserEmailAndDeletedFalse(allocation.getAssignedTo());
                if (targetUserByEmail.isPresent()) {
                    recipientEmail = targetUserByEmail.get().getUserEmail();
                }
            }
            if (recipientEmail != null) {
                notificationService.sendAllocationEmail(
                    recipientEmail,
                    allocation.getAssignedTo(),
                    asset.getAssetName(),
                    asset.getAssetCode(),
                    "RETURNED"
                );
            }
        } catch (Exception ex) {
            // Log or ignore to prevent breaking the transaction
        }

        return response;
    }

    // ── GET ALL – no filters (existing, unchanged) ────────────────────────────
    @Override
    public Page<AssetAllocationResponseDTO> getAllAllocations(Pageable pageable) {
        return allocationRepository.findAllByOrderByAssignedDateDesc(pageable)
            .map(this::toDTO);
    }

    // ── GET ALL – with optional filters ──────────────────────────────────────
    @Override
    public Page<AssetAllocationResponseDTO> getAllAllocations(
            String search, String status,
            LocalDate fromDate, LocalDate toDate,
            Pageable pageable) {

        Specification<AssetAllocation> spec = buildSpec(search, status, fromDate, toDate);
        return allocationRepository.findAll(spec, pageable).map(this::toDTO);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    public AssetAllocationResponseDTO getById(Long id) {
        AssetAllocation allocation = allocationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Allocation not found with id: " + id));
        return toDTO(allocation);
    }

    // ── GET BY ASSET ──────────────────────────────────────────────────────────
    @Override
    public List<AssetAllocationResponseDTO> getAllocationsByAsset(Long assetId) {
        return allocationRepository.findByAsset_AssetIdOrderByAssignedDateDesc(assetId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── OVERVIEW ──────────────────────────────────────────────────────────────
    @Override
    public AllocationOverviewDTO getOverview() {
        LocalDate today = LocalDate.now();
        long total         = allocationRepository.count();
        long active        = allocationRepository.countByStatus("ACTIVE");
        long returned      = allocationRepository.countByStatus("RETURNED");
        long overdue       = allocationRepository.countOverdue(today);
        long awaitingReturn = allocationRepository.countAwaitingReturn(today);
        return new AllocationOverviewDTO(total, active, returned, overdue, awaitingReturn);
    }

    // ── SPECIFICATION BUILDER ─────────────────────────────────────────────────
    private Specification<AssetAllocation> buildSpec(
            String search, String status, LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                Join<AssetAllocation, Asset> assetJoin = root.join("asset", JoinType.LEFT);
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("assignedTo")), like),
                    cb.like(cb.lower(assetJoin.get("assetName")), like),
                    cb.like(cb.lower(assetJoin.get("assetCode")), like)
                ));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.toUpperCase()));
            }

            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("assignedDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("assignedDate"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────
    private AssetAllocationResponseDTO toDTO(AssetAllocation a) {
        AssetAllocationResponseDTO dto = new AssetAllocationResponseDTO();
        dto.setAllocationId(a.getAllocationId());
        dto.setAssetId(a.getAsset().getAssetId());
        dto.setAssetName(a.getAsset().getAssetName());
        dto.setAssetCode(a.getAsset().getAssetCode());
        dto.setLocationName(a.getAsset().getLocation() != null ? a.getAsset().getLocation().getLocationName() : null);
        dto.setAssignedTo(a.getAssignedTo());
        dto.setAssignedBy(a.getAssignedBy());
        dto.setAssignedDate(a.getAssignedDate());
        dto.setExpectedReturnDate(a.getExpectedReturnDate());
        dto.setReturnDate(a.getReturnDate());
        dto.setRemarks(a.getRemarks());
        dto.setStatus(a.getStatus());
        if (a.getAsset() != null) {
            dto.setAssetImagePath(a.getAsset().getImagePath());
        }
        return dto;
    }
}