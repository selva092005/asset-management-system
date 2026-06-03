package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.demo.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

    List<Notification> findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(String email);

    long countByUserEmailAndIsReadFalse(String email);
}
