package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.model.Notification;
import com.learn.demo.model.User;
import com.learn.demo.model.AssetTransfer;
import com.learn.demo.repository.NotificationRepository;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.service.NotificationService;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.config.NotificationWebSocketHandler;
import com.learn.demo.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationWebSocketHandler webSocketHandler;
    private final EmailService emailService;
    private final com.learn.demo.service.PdfGeneratorService pdfGeneratorService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @org.springframework.beans.factory.annotation.Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    @jakarta.annotation.PostConstruct
    public void init() {
        com.learn.demo.util.EmailTemplateBuilder.setBackendUrl(backendUrl);
    }

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public List<Notification> getMyNotifications() {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(getLoggedInEmail());
    }

    @Override
    public long getMyUnreadCount() {
        return notificationRepository.countByUserEmailAndIsReadFalse(getLoggedInEmail());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(getLoggedInEmail());
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void clearAllNotifications() {
        List<Notification> all = notificationRepository.findByUserEmailOrderByCreatedAtDesc(getLoggedInEmail());
        notificationRepository.deleteAll(all);
    }

    @Override
    @Transactional
    public void sendNotification(String message, String userEmail) {
        sendNotification(message, userEmail, true);
    }

    @Override
    @Transactional
    public void sendNotification(String message, String userEmail, boolean sendEmail) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserEmail(userEmail);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        if (sendEmail) {
            String htmlContent = com.learn.demo.util.EmailTemplateBuilder.buildGeneralEmail(
                "AMS Notification Alert",
                message,
                frontendUrl
            );

            if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()) {
                org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            webSocketHandler.sendNotificationToUser(userEmail, message);
                            emailService.sendHtmlEmail(userEmail, "AMS Alert — New Notification", htmlContent);
                        }
                    }
                );
            } else {
                webSocketHandler.sendNotificationToUser(userEmail, message);
                emailService.sendHtmlEmail(userEmail, "AMS Alert — New Notification", htmlContent);
            }
        } else {
            if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()) {
                org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            webSocketHandler.sendNotificationToUser(userEmail, message);
                        }
                    }
                );
            } else {
                webSocketHandler.sendNotificationToUser(userEmail, message);
            }
        }
    }

    @Override
    @Transactional
    public void notifyAdmins(String message) {
        List<User> admins = userRepository.findByUserRoleAndDeletedFalse("ADMIN");
        for (User user : admins) {
            sendNotification(message, user.getUserEmail());
        }
    }

    @Override
    @Transactional
    public void notifyAdminsWithTransfer(AssetTransfer transfer) {
        String msg = String.format(
            "New transfer request for asset '%s' from '%s' to '%s' (Priority: %s) requested by %s.",
            transfer.getAsset().getAssetName(),
            transfer.getFromLocation(),
            transfer.getToLocation(),
            transfer.getPriority(),
            transfer.getRequestedBy()
        );

        List<User> admins = userRepository.findByUserRoleAndDeletedFalse("ADMIN");
        for (User user : admins) {
                Notification notification = new Notification();
                notification.setMessage(msg);
                notification.setUserEmail(user.getUserEmail());
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);

                String htmlContent = com.learn.demo.util.EmailTemplateBuilder.buildTransferDetailEmail(
                    "AMS Alert — Transfer Authorization Required",
                    transfer.getAsset().getAssetName(),
                    transfer.getAsset().getAssetCode() != null ? transfer.getAsset().getAssetCode() : "N/A",
                    transfer.getFromLocation(),
                    transfer.getToLocation(),
                    transfer.getPriority(),
                    transfer.getExpectedDate() != null ? transfer.getExpectedDate().toString() : "N/A",
                    transfer.getReason(),
                    transfer.getRequestedBy(),
                    transfer.getStatus(),
                    frontendUrl
                );

                if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()) {
                    org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                        new org.springframework.transaction.support.TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                webSocketHandler.sendNotificationToUser(user.getUserEmail(), msg);
                                emailService.sendHtmlEmail(user.getUserEmail(), "AMS Alert — Transfer Authorization Required", htmlContent);
                            }
                        }
                    );
                } else {
                    webSocketHandler.sendNotificationToUser(user.getUserEmail(), msg);
                    emailService.sendHtmlEmail(user.getUserEmail(), "AMS Alert — Transfer Authorization Required", htmlContent);
                }
        }
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendAllocationEmail(String employeeEmail, String employeeName, String assetName, String assetCode, String actionType, String assignedBy) {
        try {
            // Sleep 1 second to allow the transaction to commit and avoid mail concurrency issues
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            String transactionId = String.valueOf(System.currentTimeMillis());
            String dateStr = LocalDateTime.now().toString().substring(0, 19).replace("T", " ");

            // 1. Build the HTML body for the email itself
            String htmlContent = com.learn.demo.util.EmailTemplateBuilder.buildAssignmentEmail(
                "AMS Receipt — Asset " + actionType,
                employeeName,
                employeeEmail,
                assetName,
                assetCode != null ? assetCode : "N/A",
                actionType,
                dateStr,
                transactionId,
                assignedBy,
                frontendUrl,
                backendUrl
            );

            // 2. Build the XHTML layout for the PDF attachment
            String pdfHtml = com.learn.demo.util.EmailTemplateBuilder.buildReceiptInvoiceHtml(
                employeeName,
                employeeEmail,
                assetName,
                assetCode != null ? assetCode : "N/A",
                actionType,
                dateStr,
                transactionId
            );

            // 3. Generate the PDF invoice/receipt file
            String pdfFilename = "Receipt_" + actionType + "_" + transactionId + ".pdf";
            String pdfPath = pdfGeneratorService.generatePdfFile(pdfHtml, pdfFilename);

            // 4. Send email with the attachment
            emailService.sendHtmlEmailWithAttachment(
                employeeEmail,
                "AMS Receipt — Asset " + actionType,
                htmlContent,
                pdfPath
            );
        } catch (Exception e) {
            log.error("Error in sendAllocationEmail: ", e);
        }
    }

    @Override
    @Transactional
    public void sendOverdueNotification(String userEmail, String employeeName, String assetName, String assetCode, String expectedReturnDate, long daysOverdue) {
        try {
            String msg = String.format(
                "Notice: The asset '%s' (%s) allocated to %s is OVERDUE since %s (%d days).",
                assetName, assetCode, employeeName, expectedReturnDate, daysOverdue
            );

            Notification notification = new Notification();
            notification.setMessage(msg);
            notification.setUserEmail(userEmail);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);

            String htmlContent = com.learn.demo.util.EmailTemplateBuilder.buildOverdueAlertEmail(
                employeeName,
                assetName,
                assetCode != null ? assetCode : "N/A",
                expectedReturnDate,
                daysOverdue,
                frontendUrl
            );

            if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()) {
                org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            webSocketHandler.sendNotificationToUser(userEmail, msg);
                            emailService.sendHtmlEmailWithAttachment(userEmail, "AMS Warning — Asset Return Overdue", htmlContent, "uploads/Asset_Return_Policy.txt");
                        }
                    }
                );
            } else {
                webSocketHandler.sendNotificationToUser(userEmail, msg);
                emailService.sendHtmlEmailWithAttachment(userEmail, "AMS Warning — Asset Return Overdue", htmlContent, "uploads/Asset_Return_Policy.txt");
            }
        } catch (Exception e) {
            log.error("Error in sendOverdueNotification: ", e);
        }
    }
}
