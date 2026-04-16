package com.learn.demo.service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learn.demo.model.Asset;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.service.AssetService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    // @Autowired
    private final AssetRepository repository;    //con injection

    // public AssetService(AssetRepository repository) {
    //     this.repository = repository;
    // }

    @Override
public List<Asset> searchAssets(String name, String type, String location) {
    return repository.searchAssets(name, type, location);
}

    // CREATE
    @Override
    public Asset saveAsset(Asset asset) {
        return repository.save(asset);
    }

    @Override
    public List<Asset> saveAllAssets(List<Asset> assets) {
        return repository.saveAll(assets);
    }

    // READ ALL
    @Override
    public List<Asset> getAllAssets() {
        return repository.findAll();
    }

    // READ BY ID
    @Override
    public Asset getAssetById(Long assetId) {
        return repository.findById(assetId).orElse(null);
    }

    // UPDATE
    @Override
    public  Asset updateAsset(Long assetId, Asset newAsset) {

        // 🔍 Get existing data from DB
        Asset asset = repository.findById(assetId).orElse(null);

        if (asset != null) {

            // ✅ Update all fields
            // asset.setAssetId(newAsset.getAssetId());
            asset.setAssetName(newAsset.getAssetName());
            asset.setSerialNumber(newAsset.getSerialNumber());
            asset.setBrand(newAsset.getBrand());
            asset.setModel(newAsset.getModel());
            asset.setPurchaseDate(newAsset.getPurchaseDate());
            asset.setWarrantyExpiry(newAsset.getWarrantyExpiry());
            asset.setCost(newAsset.getCost());
            asset.setStatus(newAsset.getStatus());
            asset.setAssetCondition(newAsset.getAssetCondition());
            asset.setNotes(newAsset.getNotes());
            asset.setAssetTypeName(newAsset.getAssetTypeName());
            asset.setLocationName(newAsset.getLocationName());

            // 💾 Save updated data
            return repository.save(asset);
        }

        return null;
    }

    // DELETE
    public void deleteAsset(Long assetId) {
        repository.deleteById(assetId);
    }


}
