package com.learn.demo.service.ServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.learn.demo.dto.request.AssetRequestDTO;
import com.learn.demo.dto.response.AssetResponseDTO;
import com.learn.demo.dto.response.BulkUploadResultDTO;
import com.learn.demo.dto.response.BulkUploadHistoryResponseDTO;
import com.learn.demo.dto.response.BulkUploadResultDTO.RowIssue;
import com.learn.demo.dto.response.DashboardSummaryDTO;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.mapper.AssetMapper;
import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetType;
import com.learn.demo.model.BulkUploadHistory;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.repository.AssetTypeRepository;
import com.learn.demo.repository.BulkUploadHistoryRepository;
import com.learn.demo.service.AssetService;
import com.learn.demo.util.AssetCodeGenerator;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository repository;
    private final AssetTypeRepository assetTypeRepository;
    private final AssetMapper assetMapper;
    private final BulkUploadHistoryRepository bulkUploadHistoryRepository;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // ─────────────────────────────────────────────
    // SINGLE SAVE
    // ─────────────────────────────────────────────
    @Override
    public AssetResponseDTO saveAsset(AssetRequestDTO dto) {
        AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));

        if (dto.getSerialNumber() != null && !dto.getSerialNumber().isBlank()) {
            if (repository.existsBySerialNumber(dto.getSerialNumber()))
                throw new com.learn.demo.exception.DuplicateResourceException("Asset", "serialNumber", dto.getSerialNumber());
        } else {
            if (repository.existsByAssetNameAndLocationName(dto.getAssetName(), dto.getLocationName()))
                throw new com.learn.demo.exception.DuplicateResourceException("Asset", "name+location", dto.getAssetName() + " at " + dto.getLocationName());
        }

        Asset asset = assetMapper.toEntity(dto, assetType);
        asset.setStatus(dto.getStatus().toUpperCase());
        asset.setAssetCondition(dto.getAssetCondition() != null ? dto.getAssetCondition().toUpperCase() : null);

        String assetCode = generateAssetCode(dto.getCompanyName(), dto.getLocationName(), assetType.getTypeName(), null);
        asset.setAssetCode(assetCode);

        String qrContent = frontendUrl + "/home/assets/" + assetCode;
        asset.setQrCode(AssetCodeGenerator.generateQrCodeBase64(qrContent));

        return assetMapper.toResponseDTO(repository.save(asset));
    }

    // ─────────────────────────────────────────────
    // BULK SAVE (existing JSON bulk — unchanged)
    // ─────────────────────────────────────────────
    @Override
    public List<AssetResponseDTO> saveAllAssets(List<AssetRequestDTO> dtos) {
        Map<String, Long> localOffsetMap = new HashMap<>();

        List<Asset> assets = dtos.stream()
                .map(dto -> {
                    AssetType assetType = assetTypeRepository.findById(dto.getTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));

                    Asset asset = assetMapper.toEntity(dto, assetType);
                    asset.setStatus(dto.getStatus().toUpperCase());
                    asset.setAssetCondition(dto.getAssetCondition() != null
                            ? dto.getAssetCondition().toUpperCase() : null);

                    String assetCode = generateAssetCode(
                            dto.getCompanyName(), dto.getLocationName(), assetType.getTypeName(), localOffsetMap);
                    asset.setAssetCode(assetCode);

                    String qrContent = frontendUrl + "/home/assets/" + assetCode;
                    asset.setQrCode(AssetCodeGenerator.generateQrCodeBase64(qrContent));

                    return asset;
                })
                .collect(Collectors.toList());

        return repository.saveAll(assets)
                .stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private static final Set<String> VALID_STATUSES = Set.of("AVAILABLE", "DAMAGED", "UNDER_MAINTENANCE");

    // ─────────────────────────────────────────────
    // BULK UPLOAD FROM EXCEL FILE
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public BulkUploadResultDTO bulkUploadFromExcel(MultipartFile file) {
        List<RowIssue> errors  = new ArrayList<>();
        List<RowIssue> skipped = new ArrayList<>();
        int successCount = 0;
        int totalRows    = 0;
        Map<String, Long> localOffsetMap = new HashMap<>();
        List<Asset> validAssets = new ArrayList<>();

        Map<String, Integer> seenSerialNumbers     = new HashMap<>();
        Map<String, Integer> seenNameLocationPairs = new HashMap<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row, instruction row, and the two sample/demo rows.
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalRows++;
                int rowNum = i + 1;

                try {
                    String assetName       = getCellString(row, 0);
                    String serialNumber    = getCellString(row, 1);
                    String brand           = getCellString(row, 2);
                    String model           = getCellString(row, 3);
                    String purchaseDateStr = getCellString(row, 4);
                    String warrantyStr     = getCellString(row, 5);
                    String costStr         = getCellString(row, 6);
                    String status          = getCellString(row, 7);
                    String condition       = getCellString(row, 8);
                    String notes           = getCellString(row, 9);
                    String typeName        = getCellString(row, 10);
                    String locationName    = getCellString(row, 11);
                    String companyName     = getCellString(row, 12);

                    // ── Validate required fields ──────────────────────────
                    if (assetName == null || assetName.isBlank()) {
                        errors.add(new RowIssue(rowNum, "assetName", "Asset Name is required"));
                        continue;
                    }
                    if (status == null || status.isBlank()) {
                        errors.add(new RowIssue(rowNum, "status", "Status is required (AVAILABLE / DAMAGED / UNDER_MAINTENANCE)"));
                        continue;
                    }
                    if (locationName == null || locationName.isBlank()) {
                        errors.add(new RowIssue(rowNum, "locationName", "Location Name is required"));
                        continue;
                    }
                    if (companyName == null || companyName.isBlank()) {
                        errors.add(new RowIssue(rowNum, "companyName", "Company Name is required"));
                        continue;
                    }
                    if (typeName == null || typeName.isBlank()) {
                        errors.add(new RowIssue(rowNum, "typeName", "Type is required (IT / Furniture / Mobile / Equipment)"));
                        continue;
                    }

                    // ── Validate status value ─────────────────────────────
                    String statusUpper = status.toUpperCase(java.util.Locale.ROOT);
                    if ("ASSIGNED".equals(statusUpper)) {
                        errors.add(new RowIssue(rowNum, "status", "Status cannot be ASSIGNED via bulk upload"));
                        continue;
                    }
                    if ("DISPOSED".equals(statusUpper)) {
                        errors.add(new RowIssue(rowNum, "status", "Status cannot be DISPOSED via bulk upload. Use the Disposal page."));
                        continue;
                    }
                    if (!VALID_STATUSES.contains(statusUpper)) {
                        errors.add(new RowIssue(rowNum, "status", "Invalid status \"" + status + "\". Must be AVAILABLE, DAMAGED or UNDER_MAINTENANCE"));
                        continue;
                    }

                    // ── Duplicate check ───────────────────────────────────
                    boolean hasSerial = serialNumber != null && !serialNumber.isBlank();

                    if (hasSerial) {
                        if (seenSerialNumbers.containsKey(serialNumber)) {
                            skipped.add(new RowIssue(rowNum, "serialNumber",
                                    "Serial Number \"" + serialNumber + "\" is duplicate of Row " + seenSerialNumbers.get(serialNumber) + " in this file"));
                            continue;
                        }
                        if (repository.existsBySerialNumber(serialNumber)) {
                            skipped.add(new RowIssue(rowNum, "serialNumber",
                                    "Serial Number \"" + serialNumber + "\" already exists in the database"));
                            continue;
                        }
                        seenSerialNumbers.put(serialNumber, rowNum);
                    } else {
                        String nameLocationKey = assetName.toLowerCase() + "|" + locationName.toLowerCase();
                        if (seenNameLocationPairs.containsKey(nameLocationKey)) {
                            skipped.add(new RowIssue(rowNum, "assetName",
                                    "Asset \"" + assetName + "\" at \"" + locationName + "\" is duplicate of Row " + seenNameLocationPairs.get(nameLocationKey) + " in this file"));
                            continue;
                        }
                        if (repository.existsByAssetNameAndLocationName(assetName, locationName)) {
                            skipped.add(new RowIssue(rowNum, "assetName",
                                    "Asset \"" + assetName + "\" at \"" + locationName + "\" already exists in the database"));
                            continue;
                        }
                        seenNameLocationPairs.put(nameLocationKey, rowNum);
                    }

                    // ── Parse purchaseDate ────────────────────────────────
                    if (purchaseDateStr == null || purchaseDateStr.isBlank()) {
                        errors.add(new RowIssue(rowNum, "purchaseDate", "Purchase Date is required (YYYY-MM-DD)"));
                        continue;
                    }
                    LocalDate purchaseDate;
                    try {
                        purchaseDate = LocalDate.parse(purchaseDateStr);
                    } catch (Exception e) {
                        errors.add(new RowIssue(rowNum, "purchaseDate", "Invalid date \"" + purchaseDateStr + "\". Use format YYYY-MM-DD"));
                        continue;
                    }

                    // ── Parse warrantyExpiry (optional) ───────────────────
                    LocalDate warrantyExpiry = null;
                    if (warrantyStr != null && !warrantyStr.isBlank()) {
                        try {
                            warrantyExpiry = LocalDate.parse(warrantyStr);
                        } catch (Exception e) {
                            errors.add(new RowIssue(rowNum, "warrantyExpiry", "Invalid date \"" + warrantyStr + "\". Use format YYYY-MM-DD"));
                            continue;
                        }
                    }

                    // ── Parse cost ────────────────────────────────────────
                    Double cost;
                    try {
                        if (costStr == null || costStr.isBlank()) {
                            errors.add(new RowIssue(rowNum, "cost", "Cost is required"));
                            continue;
                        }
                        cost = Double.parseDouble(costStr);
                        if (cost < 0) {
                            errors.add(new RowIssue(rowNum, "cost", "Cost must be zero or positive"));
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        errors.add(new RowIssue(rowNum, "cost", "Invalid cost \"" + costStr + "\". Must be a number"));
                        continue;
                    }

                    // ── FIX 2: Case-insensitive typeName lookup ───────────
                    // Also detects when user typed a number (e.g. "1.0") instead of a name
                    AssetType assetType = assetTypeRepository.findByTypeNameIgnoreCase(typeName).orElse(null);
                    if (assetType == null) {
                        List<String> validTypes = assetTypeRepository.findAll()
                                .stream().map(AssetType::getTypeName).collect(Collectors.toList());
                        errors.add(new RowIssue(rowNum, "typeName",
                                "Type \"" + typeName + "\" not found. Valid values: " + String.join(", ", validTypes)));
                        continue;
                    }

                    // ── FIX 3: locationName and companyName saved as free text ──
                    // No DB lookup — any text is accepted, exactly like the Add Asset form
                    String cleanLocation = locationName.trim();
                    String cleanCompany  = companyName.trim();

                    // ── Build and save asset ──────────────────────────────
                    Asset asset = new Asset();
                    asset.setAssetName(assetName);
                    asset.setSerialNumber(serialNumber);
                    asset.setBrand(brand);
                    asset.setModel(model);
                    asset.setPurchaseDate(purchaseDate);
                    asset.setWarrantyExpiry(warrantyExpiry);
                    asset.setCost(cost);
                    asset.setStatus(statusUpper);
                    asset.setAssetCondition(condition != null && !condition.isBlank()
                            ? condition.toUpperCase() : null);
                    asset.setNotes(notes);
                    asset.setLocationName(cleanLocation);
                    asset.setCompanyName(cleanCompany);
                    asset.setAssetType(assetType);
                    asset.setImagePath(null);

                    String assetCode = generateAssetCode(cleanCompany, cleanLocation, assetType.getTypeName(), localOffsetMap);
                    asset.setAssetCode(assetCode);

                    String qrContent = frontendUrl + "/home/assets/" + assetCode;
                    asset.setQrCode(AssetCodeGenerator.generateQrCodeBase64(qrContent));

                    validAssets.add(asset);
                    successCount++;

                } catch (Exception e) {
                    errors.add(new RowIssue(i + 1, "unknown", "Unexpected error: " + e.getMessage()));
                }
            }
            repository.saveAll(validAssets);
        } catch (IOException e) {
            errors.add(new RowIssue(0, "file", "Failed to read Excel file: " + e.getMessage()));
        }

        int skippedCount = skipped.size();
        int failedCount  = errors.size();

        // ── Save Bulk Upload History record ──
        try {
            String uploadedBy = "Anonymous";
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                uploadedBy = SecurityContextHolder.getContext().getAuthentication().getName();
            }
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                filename = "Unknown_Excel_File.xlsx";
            }
            BulkUploadHistory history = new BulkUploadHistory();
            history.setFileName(filename);
            history.setUploadedBy(uploadedBy);
            history.setUploadedAt(LocalDateTime.now());
            history.setTotalRows(totalRows);
            history.setSuccessCount(successCount);
            history.setFailedCount(failedCount);
            history.setSkippedCount(skippedCount);

            if (failedCount == 0 && skippedCount == 0 && successCount > 0) {
                history.setStatus("COMPLETED");
            } else if (successCount > 0) {
                history.setStatus("COMPLETED_WITH_ERRORS");
            } else {
                history.setStatus("FAILED");
            }

            bulkUploadHistoryRepository.save(history);
        } catch (Exception e) {
            System.err.println("Failed to save upload history log: " + e.getMessage());
        }

        return new BulkUploadResultDTO(totalRows, successCount, skippedCount, failedCount, skipped, errors);
    }

    // ─────────────────────────────────────────────
    // EXPORT TO EXCEL WITH IMAGES
    // ─────────────────────────────────────────────
    @Override
    public ByteArrayOutputStream exportToExcel(String uploadDir) {
        List<Asset> assets = repository.findByDeletedFalse();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Assets");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            String[] headers = {
                "assetName*", "serialNumber", "brand", "model",
                "purchaseDate* (YYYY-MM-DD)", "warrantyExpiry (YYYY-MM-DD)",
                "cost*", "status*", "assetCondition", "notes",
                "typeName*", "locationName*", "companyName*", "image"
            };
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Drawing<?> drawing = null;

            for (int i = 0; i < assets.size(); i++) {
                Asset a = assets.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(nullSafe(a.getAssetName()));
                row.createCell(1).setCellValue(nullSafe(a.getSerialNumber()));
                row.createCell(2).setCellValue(nullSafe(a.getBrand()));
                row.createCell(3).setCellValue(nullSafe(a.getModel()));
                row.createCell(4).setCellValue(a.getPurchaseDate() != null ? a.getPurchaseDate().toString() : "");
                row.createCell(5).setCellValue(a.getWarrantyExpiry() != null ? a.getWarrantyExpiry().toString() : "");
                row.createCell(6).setCellValue(a.getCost() != null ? a.getCost().doubleValue() : 0.0);
                row.createCell(7).setCellValue(nullSafe(a.getStatus()));
                row.createCell(8).setCellValue(nullSafe(a.getAssetCondition()));
                row.createCell(9).setCellValue(nullSafe(a.getNotes()));
                row.createCell(10).setCellValue(a.getAssetType() != null ? a.getAssetType().getTypeName() : "");
                row.createCell(11).setCellValue(nullSafe(a.getLocationName()));
                row.createCell(12).setCellValue(nullSafe(a.getCompanyName()));

                // Embed image if present
                if (a.getImagePath() != null && !a.getImagePath().isBlank()) {
                    File imageFile = new File(uploadDir, a.getImagePath());
                    if (imageFile.exists() && imageFile.isFile()) {
                        try (FileInputStream fis = new FileInputStream(imageFile)) {
                            byte[] bytes = IOUtils.toByteArray(fis);
                            int pictureType = Workbook.PICTURE_TYPE_PNG;
                            String nameLower = imageFile.getName().toLowerCase();
                            if (nameLower.endsWith(".jpg") || nameLower.endsWith(".jpeg")) {
                                pictureType = Workbook.PICTURE_TYPE_JPEG;
                            } else if (nameLower.endsWith(".gif")) {
                                pictureType = org.apache.poi.common.usermodel.PictureType.GIF.ooxmlId;
                            }
                            int pictureIdx = workbook.addPicture(bytes, pictureType);

                            // Set row taller to fit thumbnail
                            row.setHeightInPoints(45);

                            if (drawing == null) {
                                drawing = sheet.createDrawingPatriarch();
                            }
                            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
                            anchor.setCol1(13); // Column N (Index 13)
                            anchor.setRow1(i + 1);
                            anchor.setCol2(14);
                            anchor.setRow2(i + 2);
                            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                            drawing.createPicture(anchor, pictureIdx);
                        } catch (Exception e) {
                            System.err.println("Failed to insert asset image in excel: " + e.getMessage());
                            row.createCell(13).setCellValue("Error loading image");
                        }
                    } else {
                        row.createCell(13).setCellValue("No image file");
                    }
                } else {
                    row.createCell(13).setCellValue("No image");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                if (i == 13) {
                    sheet.setColumnWidth(i, 20 * 256);
                } else {
                    sheet.autoSizeColumn(i);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel export: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // GENERATE TEMPLATE
    // FIX: Sample data now shows correct values
    // ─────────────────────────────────────────────
    @Override
    public ByteArrayOutputStream generateTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Assets");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {
                "assetName*", "serialNumber", "brand", "model",
                "purchaseDate* (YYYY-MM-DD)", "warrantyExpiry (YYYY-MM-DD)",
                "cost*", "status*", "assetCondition", "notes",
                "typeName*", "locationName*", "companyName*"
            };

            // ── Instructions row (row 0 is header, row 1 is instructions) ──
            CellStyle infoStyle = workbook.createCellStyle();
            infoStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            infoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font infoFont = workbook.createFont();
            infoFont.setItalic(true);
            infoStyle.setFont(infoFont);

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(22);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Instructions row
            Row infoRow = sheet.createRow(1);
            String[] instructions = {
                "Required", "Optional", "Optional", "Optional",
                "Required: YYYY-MM-DD", "Optional: YYYY-MM-DD",
                "Required: number", "Required: AVAILABLE / DAMAGED / UNDER_MAINTENANCE (ASSIGNED and DISPOSED not allowed)",
                "Optional: GOOD / FAIR / POOR", "Optional",
                "Required: IT / Furniture / Mobile / Equipment",
                "Required: any location text e.g. Office Room 1",
                "Required: HEPL / CavinKare / TCS / Wipro / Cavin Infotech"
            };
            for (int i = 0; i < instructions.length; i++) {
                Cell cell = infoRow.createCell(i);
                cell.setCellValue(instructions[i]);
                cell.setCellStyle(infoStyle);
            }

            CellStyle sampleStyle = workbook.createCellStyle();
            sampleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            sampleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            sampleStyle.setBorderBottom(BorderStyle.THIN);

            // Sample row 1
            Object[] sample1 = {
                "Dell Latitude Laptop", "SN-001", "Dell", "Latitude 5540",
                "2025-01-15", "2027-01-15",
                55000, "AVAILABLE", "GOOD", "Office use",
                "IT", "Office Room 1", "HEPL"
            };
            Row row1 = sheet.createRow(2);
            for (int i = 0; i < sample1.length; i++) {
                Cell cell = row1.createCell(i);
                if (sample1[i] instanceof Number n) cell.setCellValue(n.doubleValue());
                else cell.setCellValue(sample1[i].toString());
                cell.setCellStyle(sampleStyle);
            }

            // Sample row 2
            Object[] sample2 = {
                "HP EliteDisplay Monitor", "SN-002", "HP", "E24 G5",
                "2025-03-10", "2026-03-10",
                12000, "AVAILABLE", "FAIR", "",
                "Equipment", "Warehouse A", "TCS"
            };
            Row row2 = sheet.createRow(3);
            for (int i = 0; i < sample2.length; i++) {
                Cell cell = row2.createCell(i);
                if (sample2[i] instanceof Number n) cell.setCellValue(n.doubleValue());
                else cell.setCellValue(sample2[i].toString());
                cell.setCellStyle(sampleStyle);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate template: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // ASSET CODE GENERATION
    // ─────────────────────────────────────────────
    private String generateAssetCode(String companyName, String locationName, String typeName,
                                     Map<String, Long> localOffsetMap) {
        String prefix = AssetCodeGenerator.buildPrefix(companyName, locationName, typeName);
        int prefixLen = prefix.length();
        long maxSeq = repository.findMaxSequenceByPrefix(prefix, prefixLen);

        long nextSeq;
        if (localOffsetMap != null) {
            localOffsetMap.merge(prefix, 1L, Long::sum);
            nextSeq = maxSeq + localOffsetMap.get(prefix);
        } else {
            nextSeq = maxSeq + 1;
        }
        return String.format("%s%05d", prefix, nextSeq);
    }

    // ─────────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────────
    @Override
    public List<AssetResponseDTO> getAllAssets() {
        return repository.searchAssets(null, null, null, null, Pageable.unpaged())
                .stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    public AssetResponseDTO getAssetById(Long assetId) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        return assetMapper.toResponseDTO(asset);
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────
    @Override
    public AssetResponseDTO updateAsset(Long assetId, AssetRequestDTO dto) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        AssetType assetType = null;
        if (dto.getTypeId() != null) {
            assetType = assetTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("AssetType", dto.getTypeId()));
        }

        String requestedStatus = dto.getStatus().toUpperCase();
        if ("ASSIGNED".equals(requestedStatus)) {
            throw new com.learn.demo.exception.BusinessRuleException(
                "Cannot manually set status to ASSIGNED. Use the allocation endpoint.");
        }
        if ("DISPOSED".equals(requestedStatus)) {
            throw new com.learn.demo.exception.BusinessRuleException(
                "Cannot manually set status to DISPOSED. Use the disposal endpoint.");
        }

        assetMapper.updateEntityFromDTO(dto, asset, assetType);
        asset.setStatus(requestedStatus);
        asset.setAssetCondition(dto.getAssetCondition() != null ? dto.getAssetCondition().toUpperCase() : null);

        return assetMapper.toResponseDTO(repository.save(asset));
    }

    // ─────────────────────────────────────────────
    // DELETE (soft delete)
    // ─────────────────────────────────────────────
    @Override
    public void deleteAsset(Long assetId, String deletedBy) {
        Asset asset = repository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        asset.setDeleted(true);
        asset.setDeletedBy(deletedBy);
        asset.setDeletedAt(LocalDateTime.now());
        repository.save(asset);
    }

    // ─────────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────────
    @Override
    public Page<AssetResponseDTO> searchAssets(String keyword, String type, String location, String status, Pageable pageable) {
        return repository.searchAssets(keyword, type, location, status, pageable)
                .map(assetMapper::toResponseDTO);
    }

    // ─────────────────────────────────────────────
    // DASHBOARD SUMMARY
    // ─────────────────────────────────────────────
    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        long total          = repository.count();
        long available      = repository.countByDeletedFalseAndStatus("AVAILABLE");
        long assigned       = repository.countByDeletedFalseAndStatus("ASSIGNED");
        long damaged        = repository.countByDeletedFalseAndStatus("DAMAGED");
        long underMaintenance = repository.countByDeletedFalseAndStatus("UNDER_MAINTENANCE");

        LocalDate today  = LocalDate.now();
        LocalDate cutoff = today.plusDays(30);
        long expiring = repository.countExpiringWarranty(today, cutoff);

        Map<String, Long> byType = repository.countGroupByType().stream()
                .collect(Collectors.toMap(r -> (String) r[0], r -> (Long) r[1]));
        Map<String, Long> byLocation = repository.countGroupByLocation().stream()
                .collect(Collectors.toMap(r -> (String) r[0], r -> (Long) r[1]));
        Map<String, Long> byCompany = repository.countGroupByCompany().stream()
                .collect(Collectors.toMap(r -> (String) r[0], r -> (Long) r[1]));

        return new DashboardSummaryDTO(total, available, assigned, damaged, underMaintenance, expiring, byType, byLocation, byCompany);
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d) && !Double.isInfinite(d))
                        ? String.valueOf((long) d)
                        : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                        .getCreationHelper().createFormulaEvaluator();
                Cell evaluated = evaluator.evaluateInCell(cell);
                yield getCellString(evaluated.getRow(), col);
            }
            case BLANK   -> null;
            default      -> null;
        };
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    @Override
    public List<BulkUploadHistoryResponseDTO> getUploadHistory() {
        return bulkUploadHistoryRepository.findAllByOrderByUploadedAtDesc()
                .stream()
                .map(history -> {
                    BulkUploadHistoryResponseDTO dto = new BulkUploadHistoryResponseDTO();
                    dto.setUploadId(history.getUploadId());
                    dto.setFileName(history.getFileName());
                    dto.setUploadedBy(history.getUploadedBy());
                    dto.setUploadedAt(history.getUploadedAt());
                    dto.setTotalRows(history.getTotalRows());
                    dto.setSuccessCount(history.getSuccessCount());
                    dto.setFailedCount(history.getFailedCount());
                    dto.setSkippedCount(history.getSkippedCount());
                    dto.setStatus(history.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}