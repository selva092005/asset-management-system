package com.learn.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.demo.model.AssetType;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
}