package com.learn.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.learn.demo.dto.request.AssetAuditRequestDTO;
import com.learn.demo.dto.response.AssetAuditResponseDTO;

public interface AssetAuditService {
    AssetAuditResponseDTO createAudit(AssetAuditRequestDTO dto);
    List<AssetAuditResponseDTO> getAudits(String search, String status, LocalDate fromDate, LocalDate toDate);
    List<AssetAuditResponseDTO> getAuditsByAsset(Long assetId);
    Map<String, Long> getAuditOverview();
}
