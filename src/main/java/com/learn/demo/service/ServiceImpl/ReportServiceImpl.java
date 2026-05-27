package com.learn.demo.service.ServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.demo.dto.response.ReportSummaryDTO;
import com.learn.demo.model.AssetAllocation;
import com.learn.demo.model.AssetDisposal;
import com.learn.demo.model.AssetTransfer;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.AssetDisposalRepository;
import com.learn.demo.repository.AssetRepository;
import com.learn.demo.repository.AssetTransferRepository;
import com.learn.demo.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AssetRepository assetRepository;
    private final AssetAllocationRepository allocationRepository;
    private final AssetDisposalRepository disposalRepository;
    private final AssetTransferRepository transferRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryDTO getFullReport() {

        LocalDate today = LocalDate.now();

        // ── Asset counts ──────────────────────────────────────────────────────
        long available        = safeCount(() -> assetRepository.countByDeletedFalseAndStatus("AVAILABLE"));
        long assigned         = safeCount(() -> assetRepository.countByDeletedFalseAndStatus("ASSIGNED"));
        long disposed         = safeCount(() -> assetRepository.countByDeletedFalseAndStatus("DISPOSED"));
        long damaged          = safeCount(() -> assetRepository.countByDeletedFalseAndStatus("DAMAGED"));
        long underMaintenance = safeCount(() -> assetRepository.countByDeletedFalseAndStatus("UNDER_MAINTENANCE"));
        long totalAssets      = available + assigned + disposed + damaged + underMaintenance;

        // ── Asset by type / location / company ────────────────────────────────
        Map<String, Long> byType     = safeMap(() -> assetRepository.countGroupByType());
        Map<String, Long> byLocation = safeMap(() -> assetRepository.countGroupByLocation());
        Map<String, Long> byCompany  = safeMap(() -> assetRepository.countGroupByCompany());

        // ── Allocation counts ─────────────────────────────────────────────────
        long totalAllocations    = safeCount(() -> allocationRepository.count());
        long activeAllocations   = safeCount(() -> allocationRepository.countByStatus("ACTIVE"));
        long returnedAllocations = safeCount(() -> allocationRepository.countByStatus("RETURNED"));
        long overdueAllocations  = safeCount(() -> allocationRepository.countOverdue(today));

        // ── Transfer counts ───────────────────────────────────────────────────
        long totalTransfers    = safeCount(() -> transferRepository.count());
        long pendingTransfers  = safeCount(() -> transferRepository.countByStatus("PENDING"));
        long approvedTransfers = safeCount(() -> transferRepository.countByStatus("APPROVED"));
        long rejectedTransfers = safeCount(() -> transferRepository.countByStatus("REJECTED"));

        // ── Disposal counts ───────────────────────────────────────────────────
        long totalDisposals        = safeCount(() -> disposalRepository.count());
        Map<String, Long> byMethod = safeMap(() -> disposalRepository.countGroupByMethod());

        return new ReportSummaryDTO(
            totalAssets, available, assigned, disposed, damaged, underMaintenance,
            byType, byLocation, byCompany,
            totalAllocations, activeAllocations, returnedAllocations, overdueAllocations,
            totalTransfers, pendingTransfers, approvedTransfers, rejectedTransfers,
            totalDisposals, byMethod
        );
    }

    // ── Excel Export: Allocations ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream exportAllocationsToExcel() {
        List<AssetAllocation> list = allocationRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Allocations");
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                "Allocation ID", "Asset Code", "Asset Name", "Assigned To", 
                "Assigned By", "Assigned Date", "Expected Return Date", 
                "Return Date", "Status", "Remarks"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < list.size(); i++) {
                AssetAllocation a = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(a.getAllocationId() != null ? a.getAllocationId() : 0);
                row.createCell(1).setCellValue(a.getAsset() != null ? nullSafe(a.getAsset().getAssetCode()) : "");
                row.createCell(2).setCellValue(a.getAsset() != null ? nullSafe(a.getAsset().getAssetName()) : "");
                row.createCell(3).setCellValue(nullSafe(a.getAssignedTo()));
                row.createCell(4).setCellValue(nullSafe(a.getAssignedBy()));
                row.createCell(5).setCellValue(a.getAssignedDate() != null ? a.getAssignedDate().toString() : "");
                row.createCell(6).setCellValue(a.getExpectedReturnDate() != null ? a.getExpectedReturnDate().toString() : "");
                row.createCell(7).setCellValue(a.getReturnDate() != null ? a.getReturnDate().toString() : "");
                row.createCell(8).setCellValue(nullSafe(a.getStatus()));
                row.createCell(9).setCellValue(nullSafe(a.getRemarks()));
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Allocations Excel export: " + e.getMessage(), e);
        }
    }

    // ── Excel Export: Transfers ───────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream exportTransfersToExcel() {
        List<AssetTransfer> list = transferRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transfers");
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                "Transfer ID", "Asset Code", "Asset Name", "From Location", 
                "To Location", "Requested By", "Requested At", 
                "Resolved By", "Resolved At", "Status", "Remarks"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < list.size(); i++) {
                AssetTransfer t = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(t.getTransferId() != null ? t.getTransferId() : 0);
                row.createCell(1).setCellValue(t.getAsset() != null ? nullSafe(t.getAsset().getAssetCode()) : "");
                row.createCell(2).setCellValue(t.getAsset() != null ? nullSafe(t.getAsset().getAssetName()) : "");
                row.createCell(3).setCellValue(nullSafe(t.getFromLocation()));
                row.createCell(4).setCellValue(nullSafe(t.getToLocation()));
                row.createCell(5).setCellValue(nullSafe(t.getRequestedBy()));
                row.createCell(6).setCellValue(t.getRequestedAt() != null ? t.getRequestedAt().toString() : "");
                row.createCell(7).setCellValue(nullSafe(t.getResolvedBy()));
                row.createCell(8).setCellValue(t.getResolvedAt() != null ? t.getResolvedAt().toString() : "");
                row.createCell(9).setCellValue(nullSafe(t.getStatus()));
                row.createCell(10).setCellValue(nullSafe(t.getRemarks()));
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Transfers Excel export: " + e.getMessage(), e);
        }
    }

    // ── Excel Export: Disposals ───────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream exportDisposalsToExcel() {
        List<AssetDisposal> list = disposalRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Disposals");
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                "Disposal ID", "Asset Code", "Asset Name", "Disposal Date", 
                "Disposal Method", "Reason", "Disposed By", "Disposal Value"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < list.size(); i++) {
                AssetDisposal d = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(d.getDisposalId() != null ? d.getDisposalId() : 0);
                row.createCell(1).setCellValue(d.getAsset() != null ? nullSafe(d.getAsset().getAssetCode()) : "");
                row.createCell(2).setCellValue(d.getAsset() != null ? nullSafe(d.getAsset().getAssetName()) : "");
                row.createCell(3).setCellValue(d.getDisposalDate() != null ? d.getDisposalDate().toString() : "");
                row.createCell(4).setCellValue(nullSafe(d.getDisposalMethod()));
                row.createCell(5).setCellValue(nullSafe(d.getReason()));
                row.createCell(6).setCellValue(nullSafe(d.getDisposedBy()));
                row.createCell(7).setCellValue(d.getDisposalValue() != null ? d.getDisposalValue() : 0.0);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Disposals Excel export: " + e.getMessage(), e);
        }
    }

    // ── Helper: create header cell style ──────────────────────────────────────
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    private String nullSafe(String val) {
        return val != null ? val : "";
    }

    // ── Helper: safe count — returns 0 on any exception ──────────────────────
    private long safeCount(java.util.function.Supplier<Long> fn) {
        try { return fn.get(); } catch (Exception e) { return 0L; }
    }

    // ── Helper: safe map — returns empty map on any exception ─────────────────
    private Map<String, Long> safeMap(java.util.function.Supplier<java.util.List<Object[]>> fn) {
        try { return toMap(fn.get()); } catch (Exception e) { return new LinkedHashMap<>(); }
    }

    // ── Helper: Object[] list → Map<String, Long> ─────────────────────────────
    private Map<String, Long> toMap(java.util.List<Object[]> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        if (rows == null) return map;
        for (Object[] row : rows) {
            if (row == null || row.length < 2) continue;
            String key = row[0] != null ? row[0].toString() : "Unknown";
            Long val   = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            map.put(key, val);
        }
        return map;
    }
}

