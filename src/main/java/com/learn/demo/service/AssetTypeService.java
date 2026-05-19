package com.learn.demo.service;

import java.util.List;

import com.learn.demo.dto.request.AssetTypeRequestDTO;
import com.learn.demo.dto.response.AssetTypeResponseDTO;

public interface AssetTypeService {

    List<AssetTypeResponseDTO> getAllTypes();

    AssetTypeResponseDTO saveType(AssetTypeRequestDTO dto);

    void deleteType(Long typeId, String adminName);
}
