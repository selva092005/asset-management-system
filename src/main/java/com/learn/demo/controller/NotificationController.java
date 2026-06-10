package com.learn.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.model.Notification;
import com.learn.demo.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<Apiresponse> getMyNotifications() {
        return ResponseEntity.ok(
            new Apiresponse(200, "Notifications retrieved", service.getMyNotifications())
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Apiresponse> getMyUnreadCount() {
        return ResponseEntity.ok(
            new Apiresponse(200, "Unread count retrieved", service.getMyUnreadCount())
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Apiresponse> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok(
            new Apiresponse(200, "Notification marked as read", null)
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<Apiresponse> markAllAsRead() {
        service.markAllAsRead();
        return ResponseEntity.ok(
            new Apiresponse(200, "All notifications marked as read", null)
        );
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/clear-all")
    public ResponseEntity<Apiresponse> clearAllNotifications() {
        service.clearAllNotifications();
        return ResponseEntity.ok(
            new Apiresponse(200, "All notifications cleared", null)
        );
    }
}
