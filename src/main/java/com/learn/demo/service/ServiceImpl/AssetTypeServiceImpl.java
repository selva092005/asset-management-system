package com.learn.demo.service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learn.demo.model.AssetType;
import com.learn.demo.repository.AssetTypeRepository;
import com.learn.demo.service.AssetTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetTypeServiceImpl implements AssetTypeService {

    private final AssetTypeRepository repository;

    @Override
    public List<AssetType> getAllTypes() {
        return repository.findAll();
    }

    @Override
    public AssetType saveType(AssetType type) {
        return repository.save(type);
    }
}