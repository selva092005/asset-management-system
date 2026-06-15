package com.learn.demo.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.AssetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // ── CREATE SINGLE ────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Apiresponse> save(@Valid @RequestBody AssetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Apiresponse(
                        HttpStatus.CREATED.value(),
                        "Asset created successfully",
                        service.saveAsset(dto)));
    }

    // ── CREATE BULK (JSON array) ─────────────────────────────────────────────
    @PostMapping("/bulk")
    public ResponseEntity<Apiresponse> saveBulk(@Valid @RequestBody List<AssetRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Apiresponse(
                        HttpStatus.CREATED.value(),
                        "Assets created successfully",
                        service.saveAllAssets(dtos)));
    }

    // ── BULK UPLOAD FROM EXCEL FILE ──────────────────────────────────────────
    // POST /api/assets/bulk-excel
    // Accepts multipart/form-data with field name "file" (.xlsx / .xls)
    // Returns BulkUploadResultDTO wrapped in ApiResponse
    @PostMapping(value = "/bulk-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Apiresponse> bulkUploadExcel(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new Apiresponse(400, "Uploaded file is empty", null));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return ResponseEntity.badRequest().body(
                    new Apiresponse(400, "Only .xlsx and .xls files are supported", null));
        }

        return ResponseEntity.ok(
                new Apiresponse(
                        HttpStatus.OK.value(),
                        "Bulk upload processed",
                        service.bulkUploadFromExcel(file)));
    }

    // ── DOWNLOAD FAILED ROWS EXCEL ───────────────────────────────────────────
    @PostMapping(value = "/bulk-excel/failed-rows", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> downloadFailedRowsExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            ByteArrayOutputStream out = service.generateFailedRowsExcel(file);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename("failed_assets_rows.xlsx").build());
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── GET BULK UPLOAD HISTORY ──────────────────────────────────────────────
    @GetMapping("/upload-history")
    public ResponseEntity<Apiresponse> getUploadHistory() {
        return ResponseEntity.ok(
                new Apiresponse(
                        HttpStatus.OK.value(),
                        "Upload history retrieved",
                        service.getUploadHistory()));
    }

    // ── EXPORT ALL ASSETS TO EXCEL ───────────────────────────────────────────
    // GET /api/assets/export
    // Returns binary .xlsx file as a download
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAssets() {
        try {
            ByteArrayOutputStream out = service.exportToExcel(uploadDir);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename("assets_export.xlsx").build());
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(headers).body(bytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── DOWNLOAD BLANK UPLOAD TEMPLATE ───────────────────────────────────────
    // GET /api/assets/template
    // Returns a pre-formatted Excel template for bulk upload
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            ByteArrayOutputStream out = service.generateTemplate();
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename("asset_upload_template.xlsx").build());
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(headers).body(bytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── DASHBOARD SUMMARY ────────────────────────────────────────────
    @GetMapping("/dashboard")
    public ResponseEntity<Apiresponse> getDashboard() {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Dashboard summary", service.getDashboardSummary()));
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Apiresponse> getAllAssets() {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Assets retrieved successfully", service.getAllAssets()));
    }

    // ── READ BY ID ───────────────────────────────────────────────────────────
    @GetMapping("/{assetId}")
    public ResponseEntity<Apiresponse> getAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Asset retrieved successfully", service.getAssetById(assetId)));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{assetId}")
    public ResponseEntity<Apiresponse> updateAsset(
            @PathVariable Long assetId,
            @Valid @RequestBody AssetRequestDTO dto) {
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Asset updated successfully",
                        service.updateAsset(assetId, dto)));
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Apiresponse> deleteAsset(@PathVariable Long assetId) {
        String deletedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        service.deleteAsset(assetId, deletedBy);
        return ResponseEntity.ok(
                new Apiresponse(HttpStatus.OK.value(), "Deleted successfully", null));
    }

    // ── SEARCH + PAGINATION ──────────────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<Apiresponse> searchAssets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                new Apiresponse(
                        HttpStatus.OK.value(),
                        "Assets fetched",
                        service.searchAssets(keyword, type, location, status,
                                PageRequest.of(page, size, Sort.by("assetId").ascending()))));
    }
}