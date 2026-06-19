package com.learn.demo.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetActivityLog;
import com.learn.demo.dto.response.AssetActivityLogResponseDTO;
import com.learn.demo.repository.AssetActivityLogRepository;
import com.learn.demo.service.AssetActivityLogService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetActivityLogServiceImpl implements AssetActivityLogService {

    private final AssetActivityLogRepository repository;

    @Override
    @Transactional
    public void log(Asset asset, String action, String actionBy, String details) {
        AssetActivityLog log = new AssetActivityLog();
        log.setAsset(asset);
        log.setAction(action.toUpperCase());
        log.setActionBy(actionBy != null ? actionBy : "SYSTEM");
        log.setActionDate(LocalDateTime.now());
        log.setDetails(details);
        repository.save(log);
    }

    @Override
    public List<AssetActivityLogResponseDTO> getLogsByAsset(Long assetId) {
        return repository.findByAsset_AssetIdOrderByActionDateDesc(assetId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AssetActivityLogResponseDTO toDTO(AssetActivityLog log) {
        AssetActivityLogResponseDTO dto = new AssetActivityLogResponseDTO();
        dto.setLogId(log.getLogId());
        dto.setAssetId(log.getAsset().getAssetId());
        dto.setAssetName(log.getAsset().getAssetName());
        dto.setAssetCode(log.getAsset().getAssetCode());
        dto.setAction(log.getAction());
        dto.setActionBy(log.getActionBy());
        dto.setActionDate(log.getActionDate());
        dto.setDetails(log.getDetails());
        return dto;
    }
}
