package com.learn.demo.service;

import java.util.List;

import com.learn.demo.model.Notification;

import com.learn.demo.model.AssetTransfer;

public interface NotificationService {

    List<Notification> getMyNotifications();

    long getMyUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    void clearAllNotifications();

    void sendNotification(String message, String userEmail);
    void sendNotification(String message, String userEmail, boolean sendEmail);

    void notifyAdmins(String message);

    void notifyAdminsWithTransfer(AssetTransfer transfer);

    void sendAllocationEmail(String employeeEmail, String employeeName, String assetName, String assetCode, String actionType, String assignedBy);

    void sendOverdueNotification(String userEmail, String employeeName, String assetName, String assetCode, String expectedReturnDate, long daysOverdue);
}
