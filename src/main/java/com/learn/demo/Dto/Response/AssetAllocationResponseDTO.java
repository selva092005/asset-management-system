package com.learn.demo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AssetAllocationResponseDTO {

    private Long allocationId;

    private Long assetId;
    private String assetName;
    private String assetCode;
    private String locationName;

    private String assignedTo;
    private String assignedBy;
    private LocalDate assignedDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private String remarks;
    private String status;           // "ACTIVE" | "RETURNED"
}
