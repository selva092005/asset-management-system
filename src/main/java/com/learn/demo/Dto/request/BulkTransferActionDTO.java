package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class BulkTransferActionDTO {

    @NotEmpty(message = "Transfer IDs list cannot be empty")
    private List<Long> transferIds;

    @NotBlank(message = "Resolved-by name is required")
    private String resolvedBy;

    private String remarks;

    private java.time.LocalDate receivedDate;
}
