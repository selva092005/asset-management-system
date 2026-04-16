package com.learn.demo.service;

import java.util.List;

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
    List<Asset> searchAssets(String name, String type ,String location);

    Object saveAllAssets(List<Asset> assets);


}