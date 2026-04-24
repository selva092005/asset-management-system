package com.learn.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long typeId;

    @Schema(hidden = true)
    
    private String typeName;
}