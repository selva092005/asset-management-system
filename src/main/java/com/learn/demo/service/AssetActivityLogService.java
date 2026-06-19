package com.learn.demo.service;

import java.util.List;
import com.learn.demo.model.Asset;
import com.learn.demo.dto.response.AssetActivityLogResponseDTO;

public interface AssetActivityLogService {
    void log(Asset asset, String action, String actionBy, String details);
    List<AssetActivityLogResponseDTO> getLogsByAsset(Long assetId);
}
