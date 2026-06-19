package com.learn.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetAllocation;
import com.learn.demo.model.User;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.NotificationRepository;
import com.learn.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssetScheduler {

    private final AssetAllocationRepository allocationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AssetRepository assetRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 1. Daily Overdue Asset Allocation Alerts.
     * Checks for ACTIVE allocations whose expectedReturnDate has passed.
     * Alerts are triggered via web socket and email to either the user or admin.
     */
    @Scheduled(cron = "${app.cron.overdue-check:0 0 8 * * *}")
    @Transactional
    public void checkAndAlertOverdueAssets() {
        log.info("[CRON START] checkAndAlertOverdueAssets: Scanning for overdue asset allocations...");
        try {
            LocalDate today = LocalDate.now();
            List<AssetAllocation> overdueList = allocationRepository.findOverdueAllocations(today);

            log.info("Found {} overdue asset allocations.", overdueList.size());

            for (AssetAllocation allocation : overdueList) {
                String assetName = allocation.getAsset().getAssetName();
                String assetCode = allocation.getAsset().getAssetCode();
                String assignedTo = allocation.getAssignedTo();
                LocalDate expectedDate = allocation.getExpectedReturnDate();

                String message = String.format(
                    "Notice: The asset '%s' (%s) allocated to %s is OVERDUE since %s.",
                    assetName, assetCode, assignedTo, expectedDate
                );

                // Find recipient email
                String recipientEmail = null;
                Optional<User> targetUser = userRepository.findByUserNameAndDeletedFalse(assignedTo);
                if (targetUser.isPresent()) {
                    recipientEmail = targetUser.get().getUserEmail();
                } else {
                    Optional<User> targetUserByEmail = userRepository.findByUserEmailAndDeletedFalse(assignedTo);
                    if (targetUserByEmail.isPresent()) {
                        recipientEmail = targetUserByEmail.get().getUserEmail();
                    }
                }

                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(expectedDate, today);

                if (recipientEmail != null) {
                    notificationService.sendOverdueNotification(
                        recipientEmail, assignedTo, assetName, assetCode, expectedDate.toString(), daysOverdue
                    );
                    log.info("Sent overdue notification to user: {} (Email: {})", assignedTo, recipientEmail);
                } else {
                    notificationService.notifyAdmins(message);
                    log.info("User email not found for '{}'. Overdue alert forwarded to system Admins.", assignedTo);
                }

                // Add a 1.2-second delay to avoid hitting Mailtrap SMTP rate limits (5 emails/sec)
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            log.error("[CRON ERROR] checkAndAlertOverdueAssets failed", e);
        }
        log.info("[CRON END] checkAndAlertOverdueAssets completed.");
    }

    /**
     * 2. Monthly Read Notifications Database Cleanup.
     * Deletes read notifications older than 30 days.
     */
    @Scheduled(cron = "${app.cron.db-cleanup:0 0 0 1 * *}")
    public void cleanupOldReadNotifications() {
        log.info("[CRON START] cleanupOldReadNotifications: Starting read notifications cleanup...");
        try {
            LocalDateTime cutoff = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
            notificationRepository.deleteReadNotificationsOlderThan(cutoff);
            log.info("Cleaned up read notifications older than {}", cutoff);
        } catch (Exception e) {
            log.error("[CRON ERROR] cleanupOldReadNotifications failed", e);
        }
        log.info("[CRON END] cleanupOldReadNotifications completed.");
    }

    /**
     * 3. Weekly Uploads Folder Disk Cleanup.
     * Deletes files in the upload directory older than 30 days.
     */
    @Scheduled(cron = "${app.cron.disk-cleanup:0 0 0 * * SUN}")
    public void cleanupOldUploadedFiles() {
        log.info("[CRON START] cleanupOldUploadedFiles: Starting file system disk cleanup...");
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (Files.exists(path) && Files.isDirectory(path)) {
                try (var stream = Files.list(path)) {
                    stream.filter(Files::isRegularFile)
                          .forEach(file -> {
                              try {
                                  var lastModified = Files.getLastModifiedTime(file).toInstant();
                                  var cutoff = java.time.Instant.now().minus(30, ChronoUnit.DAYS);
                                  if (lastModified.isBefore(cutoff)) {
                                      Files.delete(file);
                                      log.info("Deleted old file from disk: {}", file.getFileName());
                                  }
                              } catch (IOException e) {
                                  log.error("Failed to inspect/delete file {}", file.getFileName(), e);
                              }
                          });
                }
            } else {
                log.warn("Upload directory does not exist or is not a directory: {}", uploadDir);
            }
        } catch (Exception e) {
            log.error("[CRON ERROR] cleanupOldUploadedFiles failed", e);
        }
        log.info("[CRON END] cleanupOldUploadedFiles completed.");
    }

    /**
     * 4. Daily Warranty Expirations Check.
     * Checks for assets whose warranty will expire in 30 days.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkWarrantyExpirations() {
        log.info("[CRON START] checkWarrantyExpirations: Scanning for expiring warranties...");
        try {
            LocalDate targetDate = LocalDate.now().plusDays(30);
            List<Asset> expiringAssets = assetRepository.findExpiringWarrantyOnDate(targetDate);

            if (!expiringAssets.isEmpty()) {
                String assetList = expiringAssets.stream()
                        .map(a -> String.format("'%s' (%s)", a.getAssetName(), a.getAssetCode()))
                        .collect(Collectors.joining(", "));

                String message = String.format("WARRANTY EXPIRY WARNING: The warranty of the following %d asset(s) will expire in 30 days (on %s): %s.",
                        expiringAssets.size(), targetDate, assetList);

                notificationService.notifyAdmins(message);
                log.info("Sent warranty expiration alert for: {}", assetList);
            }
        } catch (Exception ex) {
            log.error("[CRON ERROR] checkWarrantyExpirations failed", ex);
        }
        log.info("[CRON END] checkWarrantyExpirations completed.");
    }

    /**
     * 5. Daily Low Stock Alerts Check.
     * Checks for asset categories with less than 5 available items.
     */
    @Scheduled(cron = "0 15 9 * * ?")
    public void checkLowStockAlerts() {
        log.info("[CRON START] checkLowStockAlerts: Checking for low stock levels...");
        try {
            List<Object[]> availableCounts = assetRepository.countAvailableGroupByType();
            StringBuilder lowStockAlert = new StringBuilder();
            int lowCount = 0;

            for (Object[] row : availableCounts) {
                String typeName = (String) row[0];
                Long count = (Long) row[1];

                if (count < 5) {
                    if (lowStockAlert.isEmpty()) {
                        lowStockAlert.append("LOW STOCK ALERT: The following asset categories have fallen below the threshold of 5 available items: ");
                    } else {
                        lowStockAlert.append(", ");
                    }
                    lowStockAlert.append(String.format("%s (%d available)", typeName, count));
                    lowCount++;
                }
            }

            if (lowCount > 0) {
                notificationService.notifyAdmins(lowStockAlert.toString());
                log.info("Sent low stock alert: {}", lowStockAlert.toString());
            }
        } catch (Exception ex) {
            log.error("[CRON ERROR] checkLowStockAlerts failed", ex);
        }
        log.info("[CRON END] checkLowStockAlerts completed.");
    }

    /**
     * 6. Daily Preventive Maintenance Alerts Check.
     * Checks for assets that haven't had a maintenance checkup in over 180 days.
     */
    @Scheduled(cron = "0 30 9 * * ?")
    public void checkPreventiveMaintenanceAlerts() {
        log.info("[CRON START] checkPreventiveMaintenanceAlerts: Scanning for assets needing preventive maintenance...");
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(180);
            List<Asset> overdueAssets = assetRepository.findAssetsOverdueForMaintenance(cutoffDate);

            if (!overdueAssets.isEmpty()) {
                String assetList = overdueAssets.stream()
                        .map(a -> String.format("'%s' (%s)", a.getAssetName(), a.getAssetCode()))
                        .collect(Collectors.joining(", "));

                String message = String.format("PREVENTIVE MAINTENANCE WARNING: The following %d asset(s) have not had any maintenance checkup in the last 180 days: %s.",
                        overdueAssets.size(), assetList);

                notificationService.notifyAdmins(message);
                log.info("Sent preventive maintenance alert for: {}", assetList);
            }
        } catch (Exception ex) {
            log.error("[CRON ERROR] checkPreventiveMaintenanceAlerts failed", ex);
        }
        log.info("[CRON END] checkPreventiveMaintenanceAlerts completed.");
    }
}
