package com.learn.demo.service;

import java.util.List;
import com.learn.demo.dto.response.AssetMaintenanceDTO;

public interface AssetMaintenanceService {
    AssetMaintenanceDTO logMaintenance(AssetMaintenanceDTO dto, String actionBy);
    List<AssetMaintenanceDTO> getMaintenanceByAsset(Long assetId);
    List<AssetMaintenanceDTO> getAllMaintenance(String search, String outcome);
}
