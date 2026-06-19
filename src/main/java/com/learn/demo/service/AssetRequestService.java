package com.learn.demo.service;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.learn.demo.dto.request.AssetRequestRequestDTO;
import com.learn.demo.dto.response.AssetRequestResponseDTO;

public interface AssetRequestService {
    AssetRequestResponseDTO createRequest(AssetRequestRequestDTO dto);

    Page<AssetRequestResponseDTO> getRequests(String search, String status, String priority, String requestType,
            String username, Pageable pageable);

    AssetRequestResponseDTO updateStatus(Long requestId, String status, String remarks, Double cost, String adminUser);

    Map<String, Long> getRequestOverview(String username);
}
