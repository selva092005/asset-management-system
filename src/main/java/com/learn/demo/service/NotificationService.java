package com.learn.demo.service;

import java.util.List;

import com.learn.demo.model.Notification;

public interface NotificationService {

    List<Notification> getMyNotifications();

    long getMyUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    void sendNotification(String message, String userEmail);

    void notifyAdmins(String message);
}
