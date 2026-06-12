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

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationWebSocketHandler webSocketHandler;
    private final EmailService emailService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

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
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserEmail(userEmail);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

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
    }

    @Override
    @Transactional
    public void notifyAdmins(String message) {
        List<User> admins = userRepository.findAll();
        for (User user : admins) {
            if ("ADMIN".equalsIgnoreCase(user.getUserRole()) && !user.isDeleted()) {
                sendNotification(message, user.getUserEmail());
            }
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

        List<User> admins = userRepository.findAll();
        for (User user : admins) {
            if ("ADMIN".equalsIgnoreCase(user.getUserRole()) && !user.isDeleted()) {
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
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendAllocationEmail(String employeeEmail, String employeeName, String assetName, String assetCode, String actionType) {
        try {
            String htmlContent = com.learn.demo.util.EmailTemplateBuilder.buildAssignmentEmail(
                "AMS Receipt — Asset " + actionType,
                employeeName,
                assetName,
                assetCode != null ? assetCode : "N/A",
                actionType,
                LocalDateTime.now().toString().substring(0, 19).replace("T", " "),
                frontendUrl
            );
            emailService.sendHtmlEmail(employeeEmail, "AMS Receipt — Asset " + actionType, htmlContent);
        } catch (Exception e) {
            // Log/ignore errors in async background mailing
        }
    }
}
