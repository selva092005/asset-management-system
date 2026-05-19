package com.learn.demo.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResultDTO {
    private int totalRows;
    private int successCount;
    private int skippedCount;
    private int failedCount;
    private List<RowIssue> skipped;
    private List<RowIssue> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RowIssue {
        private int row;
        private String field;
        private String message;
    }
}
