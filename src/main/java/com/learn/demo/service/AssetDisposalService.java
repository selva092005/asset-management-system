package com.learn.demo.service;

import java.util.List;

import com.learn.demo.dto.request.AssetDisposalRequestDTO;
import com.learn.demo.dto.response.AssetDisposalResponseDTO;

public interface AssetDisposalService {

    AssetDisposalResponseDTO dispose(AssetDisposalRequestDTO dto);

    List<AssetDisposalResponseDTO> getAllDisposals(String search, String method);

    AssetDisposalResponseDTO getDisposalById(Long disposalId);
}
