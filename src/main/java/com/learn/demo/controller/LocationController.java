package com.learn.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.request.LocationRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.LocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // MANAGER only — add location
    @PostMapping
    public ResponseEntity<Apiresponse> save(@Valid @RequestBody LocationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Location created", locationService.saveLocation(dto))
        );
    }

    // MANAGER + ADMIN — view all
    @GetMapping
    public ResponseEntity<Apiresponse> getAll() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "All locations", locationService.getAllLocations())
        );
    }

    // MANAGER + ADMIN — view by company
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Apiresponse> getByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Locations by company", locationService.getLocationsByCompany(companyId))
        );
    }

    // MANAGER + ADMIN — view by id
    @GetMapping("/{id}")
    public ResponseEntity<Apiresponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Location found", locationService.getLocationById(id))
        );
    }

    // MANAGER only — update
    @PutMapping("/{id}")
    public ResponseEntity<Apiresponse> update(@PathVariable Long id, @Valid @RequestBody LocationRequestDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Location updated", locationService.updateLocation(id, dto))
        );
    }

    // MANAGER only — delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Apiresponse> delete(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Location deleted", null)
        );
    }
}
