package com.learn.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.Dto.Response.Apiresponse;
import com.learn.demo.model.AssetType;
import com.learn.demo.service.AssetTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/types")
@RequiredArgsConstructor
public class AssetTypeController {

    private final AssetTypeService service;

    // ✅ GET ALL TYPES
    @GetMapping
    public ResponseEntity<Apiresponse> getAllTypes() {
        return ResponseEntity.ok(
            new Apiresponse(
                HttpStatus.OK.value(),
                "Asset types fetched successfully",
                service.getAllTypes()
            )
        );
    }

    // ✅ ADD TYPE
    @PostMapping
    public ResponseEntity<Apiresponse> createType(@RequestBody AssetType type) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Asset type created successfully",
                service.saveType(type)
            )
        );
    }
}