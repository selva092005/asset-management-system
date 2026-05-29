package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.BulkUploadHistory;

public interface BulkUploadHistoryRepository extends JpaRepository<BulkUploadHistory, Long> {

    List<BulkUploadHistory> findAllByOrderByUploadedAtDesc();
}
