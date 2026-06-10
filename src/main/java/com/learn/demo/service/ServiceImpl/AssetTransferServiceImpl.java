package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.dto.request.AssetTransferActionDTO;
import com.learn.demo.dto.request.AssetTransferRequestDTO;
import com.learn.demo.dto.response.AssetTransferResponseDTO;
import com.learn.demo.dto.response.TransferOverviewDTO;
import com.learn.demo.exception.BusinessRuleException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetLocationHistory;
import com.learn.demo.model.AssetTransfer;
import com.learn.demo.model.Location;
import com.learn.demo.model.User;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.AssetLocationHistoryRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.repository.AssetTransferRepository;
import com.learn.demo.repository.LocationRepository;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.service.AssetTransferService;
import com.learn.demo.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetTransferServiceImpl implements AssetTransferService {

    private final AssetTransferRepository transferRepository;
    private final AssetRepository assetRepository;
    private final AssetLocationHistoryRepository locationHistoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final AssetAllocationRepository allocationRepository;
    private final NotificationService notificationService;

    // ── REQUEST TRANSFER ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetTransferResponseDTO requestTransfer(AssetTransferRequestDTO dto) {

        Asset asset = assetRepository.findById(dto.getAssetId())
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + dto.getAssetId()));

        if ("DISPOSED".equalsIgnoreCase(asset.getStatus())) {
            throw new BusinessRuleException("Cannot transfer a disposed asset.");
        }

        // 1. Block transfer of actively allocated assets
        boolean isAllocated = allocationRepository.existsByAsset_AssetIdAndStatus(dto.getAssetId(), "ACTIVE");
        if (isAllocated) {
            throw new BusinessRuleException("Cannot transfer an asset that is currently allocated. Please return the asset first.");
        }

        // Check no other pending transfer exists for this asset
        boolean hasPending = transferRepository
            .findByAsset_AssetIdOrderByRequestedAtDesc(dto.getAssetId())
            .stream()
            .anyMatch(t -> "PENDING".equalsIgnoreCase(t.getStatus()));
        if (hasPending) {
            throw new BusinessRuleException("A pending transfer already exists for this asset.");
        }

        // 2. Validate multi-tenancy: Destination location must belong to the same company as the asset
        List<Location> matchingLocations = locationRepository.findAllByLocationNameIgnoreCase(dto.getToLocation());
        if (matchingLocations.isEmpty()) {
            throw new ResourceNotFoundException("Destination location not found: " + dto.getToLocation());
        }

        // Uniquely resolve the location that belongs to the asset's company
        Location destLoc = matchingLocations.stream()
            .filter(l -> l.getCompany() != null && l.getCompany().getCompanyName() != null 
                    && l.getCompany().getCompanyName().equalsIgnoreCase(asset.getCompanyName()))
            .findFirst()
            .orElse(matchingLocations.get(0));

        if (asset.getCompanyName() != null && destLoc.getCompany() != null) {
            if (!asset.getCompanyName().equalsIgnoreCase(destLoc.getCompany().getCompanyName())) {
                throw new BusinessRuleException("Cannot transfer asset to a location belonging to a different company.");
            }
        }

        // toLocation must differ from current location
        if (dto.getToLocation().equalsIgnoreCase(asset.getLocationName())) {
            throw new BusinessRuleException("Destination location must differ from the current location.");
        }

        // 3. Secure Requester Audit: Resolve authentic requester from the security context
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        AssetTransfer transfer = new AssetTransfer();
        transfer.setAsset(asset);
        // Fallback to "UNASSIGNED" if asset location is null to satisfy DB NOT NULL constraint
        transfer.setFromLocation(asset.getLocationName() != null ? asset.getLocationName() : "UNASSIGNED");
        transfer.setToLocation(dto.getToLocation());
        transfer.setReason(dto.getReason());
        transfer.setExpectedDate(dto.getExpectedDate());
        transfer.setPriority(dto.getPriority() != null ? dto.getPriority().toUpperCase() : "MEDIUM");
        // Fallback to email if userName is null to satisfy DB NOT NULL constraint
        transfer.setRequestedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        transfer.setRequestedAt(LocalDateTime.now());

        if ("ADMIN".equalsIgnoreCase(currentUser.getUserRole())) {
            // Auto-Approve for Admins
            transfer.setStatus("APPROVED");
            transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            transfer.setRemarks("Auto-approved by Administrator");
            transfer.setResolvedAt(LocalDateTime.now());

            // Update asset location instantly
            String oldLocation = asset.getLocationName() != null ? asset.getLocationName() : "UNASSIGNED";
            asset.setLocationName(dto.getToLocation());
            assetRepository.save(asset);

            // Log to location history
            AssetLocationHistory history = new AssetLocationHistory();
            history.setAsset(asset);
            history.setFromLocation(oldLocation);
            history.setToLocation(dto.getToLocation());
            history.setMovedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            history.setMovedAt(LocalDateTime.now());
            history.setReason("Transfer requested and auto-approved: " + dto.getReason());
            locationHistoryRepository.save(history);
        } else {
            // Standard Flow for Managers
            transfer.setStatus("PENDING");
            
            // Notify admins
            notificationService.notifyAdmins(String.format(
                "New transfer request for asset '%s' from '%s' to '%s' (Priority: %s) requested by %s.",
                asset.getAssetName(), 
                transfer.getFromLocation(), 
                transfer.getToLocation(), 
                transfer.getPriority(),
                transfer.getRequestedBy()
            ));
        }

        return toDTO(transferRepository.save(transfer));
    }

    // ── APPROVE TRANSFER ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetTransferResponseDTO approveTransfer(Long transferId, AssetTransferActionDTO dto) {

        AssetTransfer transfer = getTransferOrThrow(transferId);

        if (!"PENDING".equalsIgnoreCase(transfer.getStatus())) {
            throw new BusinessRuleException("Only PENDING transfers can be approved.");
        }

        // Secure Resolver Audit: Resolve authentic resolver from the security context
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        transfer.setStatus("APPROVED");
        transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        transfer.setRemarks(dto.getRemarks());
        transfer.setResolvedAt(LocalDateTime.now());

        // Update asset location
        Asset asset = transfer.getAsset();
        String oldLocation = asset.getLocationName() != null ? asset.getLocationName() : "UNASSIGNED";
        asset.setLocationName(transfer.getToLocation());
        assetRepository.save(asset);

        // Log location history
        AssetLocationHistory history = new AssetLocationHistory();
        history.setAsset(asset);
        history.setFromLocation(oldLocation);
        history.setToLocation(transfer.getToLocation());
        history.setMovedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        history.setMovedAt(LocalDateTime.now());
        history.setReason("Transfer approved: " + transfer.getReason());
        locationHistoryRepository.save(history);

        // Notify requester
        String requesterEmail = null;
        java.util.Optional<User> requesterOpt = userRepository.findByUserEmailAndDeletedFalse(transfer.getRequestedBy());
        if (requesterOpt.isPresent()) {
            requesterEmail = requesterOpt.get().getUserEmail();
        } else {
            requesterEmail = userRepository.findAll().stream()
                .filter(u -> !u.isDeleted() && transfer.getRequestedBy().equalsIgnoreCase(u.getUserName()))
                .map(User::getUserEmail)
                .findFirst()
                .orElse(null);
        }
        if (requesterEmail != null) {
            notificationService.sendNotification(
                String.format("Your transfer request for asset '%s' was APPROVED by %s. Remarks: %s",
                    asset.getAssetName(), transfer.getResolvedBy(), transfer.getRemarks()),
                requesterEmail
            );
        }

        return toDTO(transferRepository.save(transfer));
    }

    // ── REJECT TRANSFER ───────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetTransferResponseDTO rejectTransfer(Long transferId, AssetTransferActionDTO dto) {

        AssetTransfer transfer = getTransferOrThrow(transferId);

        if (!"PENDING".equalsIgnoreCase(transfer.getStatus())) {
            throw new BusinessRuleException("Only PENDING transfers can be rejected.");
        }

        // Secure Resolver Audit: Resolve authentic resolver from the security context
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        transfer.setStatus("REJECTED");
        transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        transfer.setRemarks(dto.getRemarks());
        transfer.setResolvedAt(LocalDateTime.now());

        // Notify requester
        String requesterEmail = null;
        java.util.Optional<User> requesterOpt = userRepository.findByUserEmailAndDeletedFalse(transfer.getRequestedBy());
        if (requesterOpt.isPresent()) {
            requesterEmail = requesterOpt.get().getUserEmail();
        } else {
            requesterEmail = userRepository.findAll().stream()
                .filter(u -> !u.isDeleted() && transfer.getRequestedBy().equalsIgnoreCase(u.getUserName()))
                .map(User::getUserEmail)
                .findFirst()
                .orElse(null);
        }
        if (requesterEmail != null) {
            notificationService.sendNotification(
                String.format("Your transfer request for asset '%s' was REJECTED by %s. Remarks: %s",
                    transfer.getAsset().getAssetName(), transfer.getResolvedBy(), transfer.getRemarks()),
                requesterEmail
            );
        }

        return toDTO(transferRepository.save(transfer));
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<AssetTransferResponseDTO> getAllTransfers(String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return transferRepository.findByStatusOrderByRequestedAtDesc(status.toUpperCase(), pageable)
                .map(this::toDTO);
        }
        return transferRepository.findAllByOrderByRequestedAtDesc(pageable).map(this::toDTO);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public AssetTransferResponseDTO getById(Long transferId) {
        return toDTO(getTransferOrThrow(transferId));
    }

    // ── GET BY ASSET ──────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<AssetTransferResponseDTO> getTransfersByAsset(Long assetId) {
        return transferRepository.findByAsset_AssetIdOrderByRequestedAtDesc(assetId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── OVERVIEW ──────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public TransferOverviewDTO getOverview() {
        long total    = transferRepository.count();
        long pending  = transferRepository.countByStatus("PENDING");
        long approved = transferRepository.countByStatus("APPROVED");
        long rejected = transferRepository.countByStatus("REJECTED");
        return new TransferOverviewDTO(total, pending, approved, rejected);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private AssetTransfer getTransferOrThrow(Long id) {
        return transferRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));
    }

    private AssetTransferResponseDTO toDTO(AssetTransfer t) {
        AssetTransferResponseDTO dto = new AssetTransferResponseDTO();
        dto.setTransferId(t.getTransferId());
        if (t.getAsset() != null) {
            try {
                dto.setAssetId(t.getAsset().getAssetId());
                dto.setAssetName(t.getAsset().getAssetName());
                dto.setAssetCode(t.getAsset().getAssetCode());
            } catch (Exception e) {
                dto.setAssetId(null);
                dto.setAssetName("Deleted Asset");
                dto.setAssetCode("N/A");
            }
        } else {
            dto.setAssetId(null);
            dto.setAssetName("Deleted Asset");
            dto.setAssetCode("N/A");
        }
        dto.setFromLocation(t.getFromLocation());
        dto.setToLocation(t.getToLocation());
        dto.setReason(t.getReason());
        dto.setExpectedDate(t.getExpectedDate());
        dto.setPriority(t.getPriority());
        dto.setRequestedBy(t.getRequestedBy());
        dto.setResolvedBy(t.getResolvedBy());
        dto.setStatus(t.getStatus());
        dto.setRemarks(t.getRemarks());
        dto.setRequestedAt(t.getRequestedAt());
        dto.setResolvedAt(t.getResolvedAt());
        return dto;
    }
}
