package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.model.Notification;
import com.learn.demo.model.User;
import com.learn.demo.repository.NotificationRepository;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.service.NotificationService;
import com.learn.demo.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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
    public void sendNotification(String message, String userEmail) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserEmail(userEmail);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
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
}
