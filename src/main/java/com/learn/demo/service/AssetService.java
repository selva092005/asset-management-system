package com.learn.demo.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;
import com.learn.demo.dto.response.BulkUploadResultDTO;
import com.learn.demo.dto.response.DashboardSummaryDTO;

public interface AssetService {

    AssetResponseDTO saveAsset(AssetRequestDTO dto);

    List<AssetResponseDTO> saveAllAssets(List<AssetRequestDTO> dtos);

    List<AssetResponseDTO> getAllAssets();

    AssetResponseDTO getAssetById(Long assetId);

    AssetResponseDTO updateAsset(Long assetId, AssetRequestDTO dto);

    void deleteAsset(Long assetId, String adminName);

    Page<AssetResponseDTO> searchAssets(String keyword, String type, String location, String status, Pageable pageable);

    BulkUploadResultDTO bulkUploadFromExcel(MultipartFile file);

    ByteArrayOutputStream exportToExcel(String uploadDir);

    ByteArrayOutputStream generateTemplate();

    DashboardSummaryDTO getDashboardSummary();
}
