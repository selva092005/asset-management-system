package com.learn.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.AssetType;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {

    boolean existsByTypeNameIgnoreCase(String typeName);

    Optional<AssetType> findByTypeName(String typeName);

    // FIX: case-insensitive lookup so "it", "IT", "It" all work
    Optional<AssetType> findByTypeNameIgnoreCase(String typeName);
}