package com.learn.demo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BulkUploadHistoryResponseDTO {
    private Long uploadId;
    private String fileName;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private Integer totalRows;
    private Integer successCount;
    private Integer failedCount;
    private Integer skippedCount;
    private String status;
}
