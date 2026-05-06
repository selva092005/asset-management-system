package com.learn.demo.controller;

import com.learn.demo.dto.request.AssetTypeRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/types")
@RequiredArgsConstructor
public class AssetTypeController {

    private final AssetTypeService service;

    // GET ALL TYPES
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

    // ADD TYPE
    @PostMapping
    public ResponseEntity<Apiresponse> createType(@Valid @RequestBody AssetTypeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Asset type created successfully",
                service.saveType(dto)
            )
        );
    }

    // DELETE TYPE
    @DeleteMapping("/{typeId}")
    public ResponseEntity<Apiresponse> deleteType(@PathVariable Long typeId) {
        service.deleteType(typeId);
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Asset type deleted successfully", null)
        );
    }
}
