package com.learn.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.learn.demo.model.Asset;

@Service

public interface AssetService {
    //@Autowired
   Asset saveAsset(Asset asset);

    List<Asset> getAllAssets();

    Asset getAssetById(Long assetId);

    Asset updateAsset(Long assetId, Asset newAsset);

    void deleteAsset(Long assetId);


    //for search nd filter
    Page<Asset> searchAssets(String name, String type, String location, Pageable pageable);

    List<Asset> saveAllAssets(List<Asset> assets);

}