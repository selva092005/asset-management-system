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
import com.learn.demo.dto.request.BulkTransferActionDTO;
import com.learn.demo.dto.request.BulkTransferRequestDTO;
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

        String assetCompany = (asset.getLocation() != null && asset.getLocation().getCompany() != null)
            ? asset.getLocation().getCompany().getCompanyName()
            : null;
        String assetLocation = (asset.getLocation() != null)
            ? asset.getLocation().getLocationName()
            : null;

        // Uniquely resolve the location that belongs to the asset's company
        Location destLoc = matchingLocations.stream()
            .filter(l -> l.getCompany() != null && l.getCompany().getCompanyName() != null 
                    && assetCompany != null && l.getCompany().getCompanyName().equalsIgnoreCase(assetCompany))
            .findFirst()
            .orElse(matchingLocations.get(0));

        if (assetCompany != null && destLoc.getCompany() != null) {
            if (!assetCompany.equalsIgnoreCase(destLoc.getCompany().getCompanyName())) {
                throw new BusinessRuleException("Cannot transfer asset to a location belonging to a different company.");
            }
        }

        // toLocation must differ from current location
        if (dto.getToLocation().equalsIgnoreCase(assetLocation)) {
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
        transfer.setFromLocation(assetLocation != null ? assetLocation : "UNASSIGNED");
        transfer.setToLocation(dto.getToLocation());
        transfer.setReason(dto.getReason());
        transfer.setExpectedDate(dto.getExpectedDate());
        transfer.setPriority(dto.getPriority() != null ? dto.getPriority().toUpperCase() : "MEDIUM");
        // Fallback to email if userName is null to satisfy DB NOT NULL constraint
        transfer.setRequestedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        transfer.setRequestedAt(LocalDateTime.now());

        if ("ADMIN".equalsIgnoreCase(currentUser.getUserRole())) {
            // Auto-Approve for Admins - starts as IN_TRANSIT, requires receipt confirmation
            transfer.setStatus("IN_TRANSIT");
            transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            transfer.setRemarks("Auto-approved by Administrator - pending transit receipt confirmation");
            transfer.setResolvedAt(LocalDateTime.now());

            // Update asset status to IN_TRANSIT, but do not update location/history yet
            asset.setStatus("IN_TRANSIT");
            assetRepository.save(asset);
        } else {
            // Standard Flow for Managers
            transfer.setStatus("PENDING");
            
            // Notify admins with detailed HTML email template
            notificationService.notifyAdminsWithTransfer(transfer);
        }

        return toDTO(transferRepository.save(transfer));
    }

    // ── REQUEST BULK TRANSFER ──────────────────────────────────────────────────
    @Override
    @Transactional
    public List<AssetTransferResponseDTO> requestBulkTransfer(BulkTransferRequestDTO dto) {
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        List<Location> matchingLocations = locationRepository.findAllByLocationNameIgnoreCase(dto.getToLocation());
        if (matchingLocations.isEmpty()) {
            throw new ResourceNotFoundException("Destination location not found: " + dto.getToLocation());
        }

        List<AssetTransfer> savedTransfers = new java.util.ArrayList<>();

        for (Long assetId : dto.getAssetIds()) {
            Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

            if ("DISPOSED".equalsIgnoreCase(asset.getStatus())) {
                throw new BusinessRuleException("Cannot transfer a disposed asset: " + asset.getAssetName());
            }

            boolean isAllocated = allocationRepository.existsByAsset_AssetIdAndStatus(assetId, "ACTIVE");
            if (isAllocated) {
                throw new BusinessRuleException("Cannot transfer an asset that is currently allocated: " + asset.getAssetName());
            }

            boolean hasPendingOrTransit = transferRepository
                .findByAsset_AssetIdOrderByRequestedAtDesc(assetId)
                .stream()
                .anyMatch(t -> "PENDING".equalsIgnoreCase(t.getStatus()) || "IN_TRANSIT".equalsIgnoreCase(t.getStatus()));
            if (hasPendingOrTransit) {
                throw new BusinessRuleException("A pending or in-transit transfer already exists for asset: " + asset.getAssetName());
            }

            String assetCompany = (asset.getLocation() != null && asset.getLocation().getCompany() != null)
                ? asset.getLocation().getCompany().getCompanyName()
                : null;
            String assetLocation = (asset.getLocation() != null)
                ? asset.getLocation().getLocationName()
                : null;

            Location destLoc = matchingLocations.stream()
                .filter(l -> l.getCompany() != null && l.getCompany().getCompanyName() != null 
                        && assetCompany != null && l.getCompany().getCompanyName().equalsIgnoreCase(assetCompany))
                .findFirst()
                .orElse(matchingLocations.get(0));

            if (assetCompany != null && destLoc.getCompany() != null) {
                if (!assetCompany.equalsIgnoreCase(destLoc.getCompany().getCompanyName())) {
                    throw new BusinessRuleException("Cannot transfer asset '" + asset.getAssetName() + "' to a location belonging to a different company.");
                }
            }

            if (dto.getToLocation().equalsIgnoreCase(assetLocation)) {
                continue; // Gracefully skip this asset as it is already at the destination location
            }

            AssetTransfer transfer = new AssetTransfer();
            transfer.setAsset(asset);
            transfer.setFromLocation(assetLocation != null ? assetLocation : "UNASSIGNED");
            transfer.setToLocation(dto.getToLocation());
            transfer.setReason(dto.getReason());
            transfer.setExpectedDate(dto.getExpectedDate());
            transfer.setPriority(dto.getPriority() != null ? dto.getPriority().toUpperCase() : "MEDIUM");
            transfer.setRequestedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            transfer.setRequestedAt(LocalDateTime.now());

            if ("ADMIN".equalsIgnoreCase(currentUser.getUserRole())) {
                transfer.setStatus("IN_TRANSIT");
                transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
                transfer.setRemarks("Auto-approved by Administrator - pending transit receipt confirmation");
                transfer.setResolvedAt(LocalDateTime.now());

                asset.setStatus("IN_TRANSIT");
                assetRepository.save(asset);
            } else {
                transfer.setStatus("PENDING");
            }

            savedTransfers.add(transferRepository.save(transfer));
        }

        if (savedTransfers.isEmpty()) {
            throw new BusinessRuleException("All selected assets are already located at the destination location.");
        }

        if (!"ADMIN".equalsIgnoreCase(currentUser.getUserRole())) {
            for (AssetTransfer t : savedTransfers) {
                notificationService.notifyAdminsWithTransfer(t);
            }
        }

        return savedTransfers.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
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

        transfer.setStatus("IN_TRANSIT");
        transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        transfer.setRemarks(dto.getRemarks());
        transfer.setResolvedAt(LocalDateTime.now());

        // Update asset status to IN_TRANSIT, but do NOT change location or log history yet
        Asset asset = transfer.getAsset();
        asset.setStatus("IN_TRANSIT");
        assetRepository.save(asset);

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
                String.format("Your transfer request for asset '%s' was approved and is now IN_TRANSIT. Remarks: %s",
                    asset.getAssetName(), transfer.getRemarks()),
                requesterEmail
            );
        }

        return toDTO(transferRepository.save(transfer));
    }

    // ── RECEIVE TRANSFER ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public AssetTransferResponseDTO receiveTransfer(Long transferId, AssetTransferActionDTO dto) {

        AssetTransfer transfer = getTransferOrThrow(transferId);

        if (!"IN_TRANSIT".equalsIgnoreCase(transfer.getStatus())) {
            throw new BusinessRuleException("Only IN_TRANSIT transfers can be received.");
        }

        // Secure Receiver Audit: Resolve authentic receiver from the security context
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        // Complete the transfer to APPROVED status
        transfer.setStatus("APPROVED");
        transfer.setRemarks(dto.getRemarks() != null && !dto.getRemarks().isBlank() ? dto.getRemarks() : "Confirmed Receipt");
        transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        transfer.setResolvedAt(LocalDateTime.now());

        // Update asset location and restore status to AVAILABLE
        Asset asset = transfer.getAsset();
        String assetCompany = (asset.getLocation() != null && asset.getLocation().getCompany() != null)
            ? asset.getLocation().getCompany().getCompanyName()
            : "";
        String oldLocation = asset.getLocation() != null ? asset.getLocation().getLocationName() : "UNASSIGNED";
        Location destination = locationRepository.findByLocationNameIgnoreCaseAndCompany_CompanyNameIgnoreCase(
            transfer.getToLocation(),
            assetCompany
        ).orElseThrow(() -> new ResourceNotFoundException("Location not found with name: " + transfer.getToLocation()));

        asset.setLocation(destination);
        asset.setCompany(destination.getCompany());
        asset.setStatus("AVAILABLE");
        assetRepository.save(asset);

        // Log location history
        AssetLocationHistory history = new AssetLocationHistory();
        history.setAsset(asset);
        history.setFromLocation(oldLocation);
        history.setToLocation(transfer.getToLocation());
        history.setMovedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
        history.setMovedAt(LocalDateTime.now());
        history.setReason("Transfer completed: " + transfer.getReason());
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
                String.format("Your transfer request for asset '%s' was RECEIVED and COMPLETED by %s. Remarks: %s",
                    asset.getAssetName(), transfer.getResolvedBy(), transfer.getRemarks()),
                requesterEmail
            );
        }

        return toDTO(transferRepository.save(transfer));
    }

    // ── APPROVE BULK TRANSFERS ────────────────────────────────────────────────
    @Override
    @Transactional
    public List<AssetTransferResponseDTO> approveBulkTransfers(BulkTransferActionDTO dto) {
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        List<AssetTransfer> approvedTransfers = new java.util.ArrayList<>();

        for (Long id : dto.getTransferIds()) {
            AssetTransfer transfer = getTransferOrThrow(id);

            if (!"PENDING".equalsIgnoreCase(transfer.getStatus())) {
                throw new BusinessRuleException("Only PENDING transfers can be approved. Transfer ID " + id + " is in " + transfer.getStatus() + " status.");
            }

            transfer.setStatus("IN_TRANSIT");
            transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            transfer.setRemarks(dto.getRemarks());
            transfer.setResolvedAt(LocalDateTime.now());

            Asset asset = transfer.getAsset();
            asset.setStatus("IN_TRANSIT");
            assetRepository.save(asset);

            approvedTransfers.add(transferRepository.save(transfer));

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
                    String.format("Your transfer request for asset '%s' was approved and is now IN_TRANSIT. Remarks: %s",
                        asset.getAssetName(), transfer.getRemarks()),
                    requesterEmail
                );
            }
        }

        return approvedTransfers.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ── REJECT BULK TRANSFERS ─────────────────────────────────────────────────
    @Override
    @Transactional
    public List<AssetTransferResponseDTO> rejectBulkTransfers(BulkTransferActionDTO dto) {
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        List<AssetTransfer> rejectedTransfers = new java.util.ArrayList<>();

        for (Long id : dto.getTransferIds()) {
            AssetTransfer transfer = getTransferOrThrow(id);

            if (!"PENDING".equalsIgnoreCase(transfer.getStatus())) {
                throw new BusinessRuleException("Only PENDING transfers can be rejected. Transfer ID " + id + " is in " + transfer.getStatus() + " status.");
            }

            transfer.setStatus("REJECTED");
            transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            transfer.setRemarks(dto.getRemarks());
            transfer.setResolvedAt(LocalDateTime.now());

            rejectedTransfers.add(transferRepository.save(transfer));

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
        }

        return rejectedTransfers.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ── RECEIVE BULK TRANSFERS ────────────────────────────────────────────────
    @Override
    @Transactional
    public List<AssetTransferResponseDTO> receiveBulkTransfers(BulkTransferActionDTO dto) {
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUserEmailAndDeletedFalse(loggedInEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Logged-in user details not found."));

        List<AssetTransfer> receivedTransfers = new java.util.ArrayList<>();

        for (Long id : dto.getTransferIds()) {
            AssetTransfer transfer = getTransferOrThrow(id);

            if (!"IN_TRANSIT".equalsIgnoreCase(transfer.getStatus())) {
                throw new BusinessRuleException("Only IN_TRANSIT transfers can be received. Transfer ID " + id + " is in " + transfer.getStatus() + " status.");
            }

            transfer.setStatus("APPROVED");
            transfer.setRemarks(dto.getRemarks() != null && !dto.getRemarks().isBlank() ? dto.getRemarks() : "Confirmed Receipt");
            transfer.setResolvedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            transfer.setResolvedAt(LocalDateTime.now());

            Asset asset = transfer.getAsset();
            String assetCompany = (asset.getLocation() != null && asset.getLocation().getCompany() != null)
                ? asset.getLocation().getCompany().getCompanyName()
                : "";
            String oldLocation = asset.getLocation() != null ? asset.getLocation().getLocationName() : "UNASSIGNED";
            Location destination = locationRepository.findByLocationNameIgnoreCaseAndCompany_CompanyNameIgnoreCase(
                transfer.getToLocation(),
                assetCompany
            ).orElseThrow(() -> new ResourceNotFoundException("Location not found with name: " + transfer.getToLocation()));

            asset.setLocation(destination);
            asset.setCompany(destination.getCompany());
            asset.setStatus("AVAILABLE");
            assetRepository.save(asset);

            // Log location history
            AssetLocationHistory history = new AssetLocationHistory();
            history.setAsset(asset);
            history.setFromLocation(oldLocation);
            history.setToLocation(transfer.getToLocation());
            history.setMovedBy(currentUser.getUserName() != null ? currentUser.getUserName() : currentUser.getUserEmail());
            history.setMovedAt(LocalDateTime.now());
            history.setReason("Transfer completed: " + transfer.getReason());
            locationHistoryRepository.save(history);

            receivedTransfers.add(transferRepository.save(transfer));

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
                    String.format("Your transfer request for asset '%s' was RECEIVED and COMPLETED by %s. Remarks: %s",
                        asset.getAssetName(), transfer.getResolvedBy(), transfer.getRemarks()),
                    requesterEmail
                );
            }
        }

        return receivedTransfers.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
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

    @Override
    @Transactional(readOnly = true)
    public Page<AssetTransferResponseDTO> getAllTransfers(String status, java.time.LocalDate startDate, java.time.LocalDate endDate, Pageable pageable) {
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;
        if (startDate != null) {
            start = startDate.atStartOfDay();
        }
        if (endDate != null) {
            end = endDate.atTime(23, 59, 59, 999999999);
        }
        String statusUpper = (status != null && !status.isBlank()) ? status.toUpperCase() : null;

        return transferRepository.findByFilters(statusUpper, start, end, pageable)
            .map(this::toDTO);
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
        long total     = transferRepository.count();
        long pending   = transferRepository.countByStatus("PENDING");
        long approved  = transferRepository.countByStatus("APPROVED");
        long rejected  = transferRepository.countByStatus("REJECTED");
        long inTransit = transferRepository.countByStatus("IN_TRANSIT");
        return new TransferOverviewDTO(total, pending, approved, rejected, inTransit);
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

    @Override
    @Transactional(readOnly = true)
    public java.io.ByteArrayOutputStream exportToExcel() {
        List<AssetTransfer> transfers = transferRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "requestedAt"));

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Asset Transfers Log");

            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            String[] headers = { "Transfer ID", "Asset Name", "Asset Code", "From Location", "To Location", "Priority", "Status", "Reason", "Requested By", "Requested At", "Resolved By", "Resolved At", "Remarks" };
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < transfers.size(); i++) {
                AssetTransfer t = transfers.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(t.getTransferId() != null ? t.getTransferId().toString() : "");
                row.createCell(1).setCellValue(t.getAsset() != null ? t.getAsset().getAssetName() : "Deleted Asset");
                row.createCell(2).setCellValue(t.getAsset() != null ? t.getAsset().getAssetCode() : "N/A");
                row.createCell(3).setCellValue(t.getFromLocation() != null ? t.getFromLocation() : "");
                row.createCell(4).setCellValue(t.getToLocation() != null ? t.getToLocation() : "");
                row.createCell(5).setCellValue(t.getPriority() != null ? t.getPriority() : "");
                row.createCell(6).setCellValue(t.getStatus() != null ? t.getStatus() : "");
                row.createCell(7).setCellValue(t.getReason() != null ? t.getReason() : "");
                row.createCell(8).setCellValue(t.getRequestedBy() != null ? t.getRequestedBy() : "");
                row.createCell(9).setCellValue(t.getRequestedAt() != null ? t.getRequestedAt().toString().substring(0, 19).replace("T", " ") : "");
                row.createCell(10).setCellValue(t.getResolvedBy() != null ? t.getResolvedBy() : "");
                row.createCell(11).setCellValue(t.getResolvedAt() != null ? t.getResolvedAt().toString().substring(0, 19).replace("T", " ") : "");
                row.createCell(12).setCellValue(t.getRemarks() != null ? t.getRemarks() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            workbook.write(out);
            return out;

        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to generate Transfer excel export: " + e.getMessage(), e);
        }
    }
}
