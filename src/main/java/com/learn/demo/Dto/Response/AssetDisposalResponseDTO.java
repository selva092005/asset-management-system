package com.learn.demo.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AssetDisposalResponseDTO {

    private Long disposalId;

    private Long assetId;
    private String assetName;
    private String assetCode;

    private LocalDate disposalDate;
    private String disposalMethod;
    private String reason;
    private String disposedBy;
    private Double disposalValue;
    private String assetImagePath;
}
