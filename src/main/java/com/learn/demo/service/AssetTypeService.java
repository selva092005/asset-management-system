package com.learn.demo.service;

import java.util.List;

import com.learn.demo.model.AssetType;

public interface AssetTypeService {

    List<AssetType> getAllTypes();

    AssetType saveType(AssetType type);
}