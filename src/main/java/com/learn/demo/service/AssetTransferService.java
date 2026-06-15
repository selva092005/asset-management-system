package com.learn.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.learn.demo.dto.request.AssetTransferActionDTO;
import com.learn.demo.dto.request.AssetTransferRequestDTO;
import com.learn.demo.dto.request.BulkTransferActionDTO;
import com.learn.demo.dto.request.BulkTransferRequestDTO;
import com.learn.demo.dto.response.AssetTransferResponseDTO;
import com.learn.demo.dto.response.TransferOverviewDTO;

public interface AssetTransferService {

    AssetTransferResponseDTO requestTransfer(AssetTransferRequestDTO dto);

    List<AssetTransferResponseDTO> requestBulkTransfer(BulkTransferRequestDTO dto);

    AssetTransferResponseDTO approveTransfer(Long transferId, AssetTransferActionDTO dto);

    AssetTransferResponseDTO rejectTransfer(Long transferId, AssetTransferActionDTO dto);

    AssetTransferResponseDTO receiveTransfer(Long transferId, AssetTransferActionDTO dto);

    AssetTransferResponseDTO cancelTransfer(Long transferId, AssetTransferActionDTO dto);

    List<AssetTransferResponseDTO> approveBulkTransfers(BulkTransferActionDTO dto);

    List<AssetTransferResponseDTO> rejectBulkTransfers(BulkTransferActionDTO dto);

    List<AssetTransferResponseDTO> receiveBulkTransfers(BulkTransferActionDTO dto);

    Page<AssetTransferResponseDTO> getAllTransfers(String search, String status, String priority, String requestedBy, java.time.LocalDate startDate, java.time.LocalDate endDate, Pageable pageable);

    AssetTransferResponseDTO getById(Long transferId);

    List<AssetTransferResponseDTO> getTransfersByAsset(Long assetId);

    TransferOverviewDTO getOverview();

    java.io.ByteArrayOutputStream exportToExcel();
}
