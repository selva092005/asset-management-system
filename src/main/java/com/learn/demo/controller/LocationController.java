package com.learn.demo.controller;

import com.learn.demo.dto.request.LocationRequestDTO;
import com.learn.demo.dto.request.SaveCurrentLocationRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.GeoLocationService;
import com.learn.demo.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final GeoLocationService geoLocationService;

    // ─── CURRENT LOCATION (IP-based auto-detect) ────────────────────────────────

    /**
     * GET /api/locations/current
     *
     * Detects the caller's current location from their IP address.
     * Does NOT save to database — just returns detected city/region/country/lat-lon.
     *
     * Who can call: MANAGER + ADMIN (same as other GET location endpoints)
     *
     * Example response:
     * {
     *   "city": "Chennai",
     *   "region": "Tamil Nadu",
     *   "country": "India",
     *   "countryCode": "IN",
     *   "timezone": "Asia/Kolkata",
     *   "ip": "103.x.x.x",
     *   "latitude": 13.0827,
     *   "longitude": 80.2707
     * }
     */
    @GetMapping("/current")
    public ResponseEntity<Apiresponse> getCurrentLocation(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Current location detected",
                geoLocationService.detectCurrentLocation(request))
        );
    }

    /**
     * POST /api/locations/save-current
     *
     * Saves a location to the database using the name detected or confirmed by the user.
     *
     * Two-step flow (recommended for frontend):
     *   1. Call GET /api/locations/current  → get city name from IP
     *   2. Show it to user; user can confirm OR type a different name (manual override)
     *   3. Call POST /api/locations/save-current with { locationName, companyId }
     *
     * Who can call: MANAGER only (same as POST /api/locations)
     *
     * Request body:
     * {
     *   "locationName": "Chennai",   // from auto-detect OR manually typed
     *   "companyId": 1
     * }
     */
    @PostMapping("/save-current")
    public ResponseEntity<Apiresponse> saveCurrentLocation(
            @Valid @RequestBody SaveCurrentLocationRequestDTO dto) {

        // Reuse the same LocationRequestDTO + service logic (same validation, duplicate check, etc.)
        LocationRequestDTO locationRequest = new LocationRequestDTO();
        locationRequest.setLocationName(dto.getLocationName());
        locationRequest.setCompanyId(dto.getCompanyId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Location saved successfully",
                locationService.saveLocation(locationRequest))
        );
    }

    // ─── MANUAL CRUD (unchanged from original) ──────────────────────────────────

    // MANAGER only — add location manually
    @PostMapping
    public ResponseEntity<Apiresponse> save(@Valid @RequestBody LocationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Location created",
                locationService.saveLocation(dto))
        );
    }

    // MANAGER + ADMIN — view all
    @GetMapping
    public ResponseEntity<Apiresponse> getAll() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "All locations",
                locationService.getAllLocations())
        );
    }

    // MANAGER + ADMIN — view by company
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Apiresponse> getByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Locations by company",
                locationService.getLocationsByCompany(companyId))
        );
    }

    // MANAGER + ADMIN — view by id
    @GetMapping("/{id}")
    public ResponseEntity<Apiresponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Location found",
                locationService.getLocationById(id))
        );
    }

    // MANAGER only — update
    @PutMapping("/{id}")
    public ResponseEntity<Apiresponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequestDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Location updated",
                locationService.updateLocation(id, dto))
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