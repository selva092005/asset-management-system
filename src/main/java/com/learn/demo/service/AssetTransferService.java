package com.learn.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.learn.demo.dto.request.AssetTransferActionDTO;
import com.learn.demo.dto.request.AssetTransferRequestDTO;
import com.learn.demo.dto.response.AssetTransferResponseDTO;
import com.learn.demo.dto.response.TransferOverviewDTO;

public interface AssetTransferService {

    AssetTransferResponseDTO requestTransfer(AssetTransferRequestDTO dto);

    AssetTransferResponseDTO approveTransfer(Long transferId, AssetTransferActionDTO dto);

    AssetTransferResponseDTO rejectTransfer(Long transferId, AssetTransferActionDTO dto);

    Page<AssetTransferResponseDTO> getAllTransfers(String status, Pageable pageable);

    AssetTransferResponseDTO getById(Long transferId);

    List<AssetTransferResponseDTO> getTransfersByAsset(Long assetId);

    TransferOverviewDTO getOverview();
}
