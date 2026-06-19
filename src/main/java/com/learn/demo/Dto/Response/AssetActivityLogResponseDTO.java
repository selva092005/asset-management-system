package com.learn.demo.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AssetActivityLogResponseDTO {
    private Long logId;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private String action;
    private String actionBy;
    private LocalDateTime actionDate;
    private String details;
}
