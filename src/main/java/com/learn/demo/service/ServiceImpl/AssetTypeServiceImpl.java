package com.learn.demo.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.learn.demo.dto.request.AssetTypeRequestDTO;
import com.learn.demo.dto.response.AssetTypeResponseDTO;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.mapper.AssetTypeMapper;
import com.learn.demo.model.AssetType;
import com.learn.demo.repository.AssetTypeRepository;
import com.learn.demo.service.AssetTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetTypeServiceImpl implements AssetTypeService {

    private final AssetTypeRepository repository;
    private final AssetTypeMapper assetTypeMapper;

    @Override
    public List<AssetTypeResponseDTO> getAllTypes() {
        return repository.findAll()
                .stream()
                .map(assetTypeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetTypeResponseDTO saveType(AssetTypeRequestDTO dto) {
        AssetType assetType = assetTypeMapper.toEntity(dto);
        return assetTypeMapper.toResponseDTO(repository.save(assetType));
    }

    @Override
    public void deleteType(Long typeId) {
        AssetType type = repository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("AssetType", typeId));
        type.setDeleted(true);
        repository.save(type);
    }
}
